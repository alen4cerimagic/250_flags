package com.android.flags.presentation

import android.content.SharedPreferences
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.android.flags.domain.CountryModel
import com.android.flags.domain.QuizRepository
import com.android.flags.util.Constants
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
    private val repository: QuizRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private var allCountries: MutableList<CountryModel>? = null
    private var solution: CountryModel? = null

    private var correctAnswerCounter = 0
    private var incorrectAnswerCounter = 0

    private var timer = AtomicInteger(10)
    private var timerJob: Job? = null
    private val timerFlow = flow {
        while (true) {
            delay(1000)
            emit(Unit)
        }
    }.flowOn(Dispatchers.IO).onEach {
        tick()
    }

    var currentHighScore = sharedPreferences.getInt(Constants.PREF_HIGH_SCORE, 0)

    val questions = MutableLiveData<Event<Resource<List<CountryModel>>>>()
    val correctAnswersCount = MutableLiveData<Event<Int>>()
    val time = MutableLiveData<Event<Int>>()
    val extraTime = MutableLiveData<Event<Int>>()
    val result = MutableLiveData<Event<Pair<Int, Int>>>()
    val highScore = MutableLiveData<Event<Int>>()

    fun play() {
        allCountries?.let {
            setJob()
            it.shuffle()
            prepareQuestions(it.take(4))
        } ?: getCountries()
    }

    private fun setJob() {
        if (timerJob == null)
            timerJob = createJob()
    }

    private fun createJob() = timerFlow.launchIn(viewModelScope)

    private fun tick() {
        time.value = Event(timer.decrementAndGet())
        if (timer.get() <= 0)
            endGame()
    }

    private fun prepareQuestions(list: List<CountryModel>) {
        val correctAnswerIndex = Random.nextInt(0, 4)
        for ((index, value) in list.withIndex()) {
            value.correct = correctAnswerIndex == index
        }
        solution = list[correctAnswerIndex]
        questions.value = Event(Resource(Status.SUCCESS, list))
    }

    private fun getCountries() {
        questions.value = Event(Resource(Status.LOADING, null))
        viewModelScope.launch {
            repository.getAllCountries().let {
                if (it.status == Status.SUCCESS) {
                    allCountries = it.data?.toMutableList()
                    play()
                } else
                    questions.value = Event(Resource(Status.ERROR, null))
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
        updateCounter(1)
        play()
        correctAnswersCount.value = Event(correctAnswerCounter)
    }

    private fun handleIncorrectAnswer() {
        incorrectAnswerCounter++
        updateCounter(-1)
    }

    private fun updateCounter(value: Int) {
        extraTime.value = Event(value)
        if (timer.addAndGet(value) <= 0)
            endGame()
    }

    private fun endGame() {
        timerJob?.cancel()
        timerJob = null

        if (currentHighScore < correctAnswerCounter) {
            sharedPreferences.edit().putInt(Constants.PREF_HIGH_SCORE, correctAnswerCounter).apply()

            highScore.value = Event(correctAnswerCounter)
        }
        time.value = Event(10)
        correctAnswersCount.value = Event(0)
        result.value = Event(Pair(correctAnswerCounter, incorrectAnswerCounter))

        timer.addAndGet(10)
        correctAnswerCounter = 0
        incorrectAnswerCounter = 0
    }
}