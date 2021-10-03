package com.android.flags.data

import com.android.flags.data.responses.CountryResponse
import retrofit2.Response
import retrofit2.http.GET

interface CountriesAPI {

    @GET("all")
    suspend fun getAll(): Response<List<CountryResponse>>
}