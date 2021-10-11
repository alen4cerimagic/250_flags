package com.android.flags

import com.android.flags.domain.CountryModel
import com.android.flags.domain.QuizRepository
import com.android.flags.util.Resource
import com.android.flags.util.Status

class TestQuizRepository : QuizRepository {

    private var shouldReturnNetworkError = false

    private val allCountries = listOf(
        CountryModel("A", "", "", null),
        CountryModel("B", "", "", null),
        CountryModel("C", "", "", null),
        CountryModel("D", "", "", null),
        CountryModel("E", "", "", null),
        CountryModel("F", "", "", null)
    )

    fun setShouldReturnNetworkError(value: Boolean) {
        shouldReturnNetworkError = value
    }

    override suspend fun getAllCountries(): Resource<List<CountryModel>> {
        return if (shouldReturnNetworkError) {
            Resource(Status.ERROR, null)
        } else {
            Resource(Status.SUCCESS, allCountries)
        }
    }
}