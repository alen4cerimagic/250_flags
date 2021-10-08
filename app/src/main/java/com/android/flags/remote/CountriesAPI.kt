package com.android.flags.remote

import com.android.flags.remote.dto.CountryDto
import retrofit2.Response
import retrofit2.http.GET

interface CountriesAPI {

    @GET("all")
    suspend fun getAll(): Response<List<CountryDto>>
}