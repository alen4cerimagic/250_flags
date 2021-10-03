package com.android.flags.repo

import com.android.flags.data.responses.CountryResponse
import com.android.flags.util.Resource

interface CountriesRepository {
    suspend fun getAll(): Resource<List<CountryResponse>>
}