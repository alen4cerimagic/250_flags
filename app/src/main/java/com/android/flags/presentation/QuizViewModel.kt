package com.android.flags.presentation

import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.flags.domain.QuizRepository
import com.android.flags.domain.model.CountryModel
import com.android.flags.domain.model.TextTemplatesModel
import com.android.flags.util.Event
import com.android.flags.util.Resource
import com.android.flags.util.Status
import com.android.flags.util.TextResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val repository: QuizRepository
) : ViewModel() {

    private var allCountries: MutableList<CountryModel>? = null
    private var textResources: TextTemplatesModel = repository.getTextResource()

    private var answersCounter = 0
    private var answer: CountryModel? = null

    val countries = MutableLiveData<Event<Resource<List<CountryModel>>>>()
    val message = MutableLiveData<Event<TextResource<String>>>()
    val answersCount = MutableLiveData<Event<Int>>()


    fun play() {
        allCountries?.let {
            it.shuffle()
            prepareQuestion(it[Random.nextInt(0, 4)])
            countries.value = Event(Resource.success(it.take(4)))
        } ?: getCountries()
    }

    private fun getCountries() {
        countries.value = Event(Resource.loading(null))
        viewModelScope.launch {
            repository.getAllCountries().let {
                if (it.status == Status.SUCCESS) {
                    allCountries = it.data?.toMutableList()
                    play()
                } else
                    countries.value = Event(Resource.error(it.message, null))
            }
        }
    }

    private fun prepareQuestion(country: CountryModel) {
        answer = country

        val question = textResources.questionTemplates.let {
            String.format(it[Random.nextInt(0, it.size)], country.name)
        }
        //_message.value = Event(Resource.initial(message))
    }

    fun answer(country: CountryModel) {
        when {
            answer == null -> return //already handled
            country == answer -> handleCorrectAnswer()
            else -> handleIncorrectAnswer()
        }
    }

    private fun handleCorrectAnswer() {
        answersCounter++
        val message = textResources.correctAnswerTemplates.let {
            String.format(it[Random.nextInt(0, it.size)], answer?.name, answersCounter)
        }
        answer = null
        //_message.value = Event(Resource.success(message))
    }

    private fun handleIncorrectAnswer() {
        val message = textResources.incorrectAnswerTemplates.let {
            String.format(it[Random.nextInt(0, it.size)], answersCounter)
        }
        answersCounter = 0
        answer = null
        //_message.value = Event(Resource.error(message, null))
    }

    fun greetUser() {
        val message = textResources.greetings.let {
            it[Random.nextInt(0, it.size)]
        }
        //_message.value = Event(Resource.initial(message))
    }
}