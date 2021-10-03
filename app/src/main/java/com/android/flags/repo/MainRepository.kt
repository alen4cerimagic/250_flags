package com.android.flags.repo

import com.android.flags.data.responses.CountryResponse
import com.android.flags.util.Resource

interface MainRepository {

    suspend fun getAllCountries(): Resource<List<CountryResponse>>

    fun getAllMessages() : HashMap<Int, List<String>>
}