package com.android.flags.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.flags.domain.CountryModel
import com.android.flags.domain.QuizRepository
import com.android.flags.util.Event
import com.android.flags.util.Resource
import com.android.flags.util.Status
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private var allCountries: MutableList<CountryModel>? = null

    private var correctAnswerCounter = 0
    private var incorrectAnswerCounter = 0
    private var timer = AtomicInteger(10)
    private var solution: CountryModel? = null

    val countries = MutableLiveData<Event<Resource<List<CountryModel>>>>()
    val answers = MutableLiveData<Event<Int>>()
    val time = MutableLiveData<Event<Int>>()
    val result = MutableLiveData<Event<Pair<Int, Int>>>()

    var job: Job? = null
    private val tickerFlow = flow {
        while (true) {
            delay(1000)
            emit(Unit)
        }
    }.flowOn(Dispatchers.IO).onEach {
        time.value = Event(timer.decrementAndGet())
        if (timer.get() <= 0)
            endGame()
    }

    private fun createJob() = tickerFlow.launchIn(viewModelScope)

    private fun setJob() {
        if (job == null || job?.isCompleted == true) {
            job = null
            job = createJob()
        }
    }

    fun play() {
        setJob()
        allCountries?.let {
            it.shuffle()
            prepareQuestion(it.take(4))
        } ?: getCountries()
    }

    private fun prepareQuestion(roundList: List<CountryModel>) {
        val correctAnswerIndex = Random.nextInt(0, 4)
        for ((index, value) in roundList.withIndex()) {
            value.correct = correctAnswerIndex == index
        }
        solution = roundList[correctAnswerIndex]
        countries.value = Event(Resource(Status.SUCCESS, roundList))
    }

    private fun getCountries() {
        countries.value = Event(Resource(Status.LOADING, null))
        viewModelScope.launch {
            repository.getAllCountries().let {
                if (it.status == Status.SUCCESS) {
                    allCountries = it.data?.toMutableList()
                    play()
                }
            }
        }
    }

    fun answer(country: CountryModel) {
        when (country) {
            solution -> handleCorrectAnswer()
            else -> handleIncorrectAnswer()
        }
    }

    private fun handleCorrectAnswer() {
        correctAnswerCounter++
        answers.value = Event(correctAnswerCounter)
        updateCounter(3)
        play()
    }

    private fun handleIncorrectAnswer() {
        incorrectAnswerCounter++
        updateCounter(-2)
    }

    private fun updateCounter(value: Int) {
        if (timer.addAndGet(value) <= 0)
            endGame()
    }

    private fun endGame() {
        job?.cancel()
        time.value = Event(10)
        answers.value = Event(0)
        result.value = Event(Pair(correctAnswerCounter, incorrectAnswerCounter))
        timer.addAndGet(10)
        correctAnswerCounter = 0
        incorrectAnswerCounter = 0
    }
}