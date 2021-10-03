package com.android.flags.repo

import com.android.flags.data.CountriesAPI
import com.android.flags.data.responses.CountryResponse
import com.android.flags.util.Resource
import java.lang.Exception
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val countriesAPI: CountriesAPI
) : CountriesRepository {
    override suspend fun getAll(): Resource<List<CountryResponse>> {
        return try {
            val response = countriesAPI.getAll()
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error("An unknown error occurred.", null)
            } else
                Resource.error("An unknown error occurred.", null)
        } catch (e: Exception) {
            Resource.error("Couldn't reach the server.", null)
        }
    }
}