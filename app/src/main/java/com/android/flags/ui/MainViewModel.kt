package com.android.flags.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.android.flags.data.responses.CountryResponse
import com.android.flags.repo.CountriesRepository
import com.android.flags.util.Event
import com.android.flags.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: CountriesRepository
) : ViewModel() {

    private var allCountries: MutableList<CountryResponse>? = null

    private val _countries = MutableLiveData<Event<Resource<List<CountryResponse>>>>()
    val countries: LiveData<Event<Resource<List<CountryResponse>>>> = _countries

    private fun getAllCountries() {
        _countries.value = Event(Resource.loading(null))
        viewModelScope.launch {
            allCountries = repository.getAll().data?.toMutableList()
            getCountries()
        }
    }

    fun getCountries() {
        allCountries?.let {
            it.shuffle()
            _countries.value = Event(Resource.success(it.take(9)))
        } ?: getAllCountries()
    }
}