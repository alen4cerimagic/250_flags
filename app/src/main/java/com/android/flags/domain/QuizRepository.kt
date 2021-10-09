package com.android.flags.domain

import com.android.flags.util.Resource

interface QuizRepository {

    suspend fun getAllCountries(): Resource<List<CountryModel>>
}