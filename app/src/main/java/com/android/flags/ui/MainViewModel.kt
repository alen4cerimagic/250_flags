package com.android.flags.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.flags.data.responses.CountryResponse
import com.android.flags.repo.MainRepository
import com.android.flags.util.Constants.PERIOD_BETWEEN_MESSAGES
import com.android.flags.util.Constants.MESSAGE_LEVELS
import com.android.flags.util.Event
import com.android.flags.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private var allCountries: MutableList<CountryResponse>? = null
    private var allMessages: HashMap<Int, List<String>>

    private var refreshCounter = 0
    private var counter = 0

    private val _countries = MutableLiveData<Event<Resource<List<CountryResponse>>>>()
    val countries: LiveData<Event<Resource<List<CountryResponse>>>> = _countries
    private val _message = MutableLiveData<Event<Resource<String>>>()
    val message: LiveData<Event<Resource<String>>> = _message

    init {
        allMessages = repository.getAllMessages()
    }

    private fun getAllCountries() {
        setTicker()
        _countries.value = Event(Resource.loading(null))
        viewModelScope.launch {
            allCountries = repository.getAllCountries().data?.toMutableList()
            getCountries()
        }
    }

    fun getCountries() {
        refreshCounter++
        allCountries?.let {
            it.shuffle()
            _countries.value = Event(Resource.success(it.take(9)))
        } ?: getAllCountries()
    }

    private fun setTicker() = flow<Any> {
        while (counter < MESSAGE_LEVELS) {
            emit(Unit)
            delay(PERIOD_BETWEEN_MESSAGES)
        }
    }
        .onEach {
            counter++
            emitMessage()
        }.launchIn(viewModelScope)

    fun greetUser() : String {
        return getMessage()
    }

    private fun emitMessage() {
        val message = getMessage()
        _message.value = Event(Resource.success(message))
    }

    private fun getMessage(): String {
        val messages = allMessages[counter]
        val index = Random.nextInt(0, messages!!.size)
        return if (counter == 0)
            String.format(messages[index], "Monday", "12:35h")
        else
            String.format(messages[index], counter, refreshCounter)
    }
}