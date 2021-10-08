package com.android.flags.domain

import com.android.flags.domain.model.CountryModel
import com.android.flags.domain.model.TextTemplatesModel
import com.android.flags.util.Resource

interface QuizRepository {
    suspend fun getAllCountries(): Resource<List<CountryModel>>
    fun getTextResource() : TextTemplatesModel
}