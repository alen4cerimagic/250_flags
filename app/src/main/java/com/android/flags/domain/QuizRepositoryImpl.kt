package com.android.flags.domain

import com.android.flags.remote.CountriesAPI
import com.android.flags.remote.toCountryModel
import com.android.flags.util.Resource
import com.android.flags.util.Status
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val countriesAPI: CountriesAPI
) : QuizRepository {

    override suspend fun getAllCountries(): Resource<List<CountryModel>> {
        return try {
            val response = countriesAPI.getAll()
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource(Status.SUCCESS, it.filter {
                        it.name?.common != null && it.flags?.png != null
                    }.map {
                        it.toCountryModel()
                    })
                } ?: Resource(Status.ERROR, null)
            } else Resource(Status.ERROR, null)
        } catch (e: Exception) {
            Resource(Status.ERROR, null)
        }
    }
}