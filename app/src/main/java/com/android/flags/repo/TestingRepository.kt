package com.android.flags.repo

import com.android.flags.data.responses.CountryResponse
import com.android.flags.util.Resource

class TestingRepository : MainRepository {

    private var shouldReturnNetworkError = false

    override suspend fun getAllCountries(): Resource<List<CountryResponse>> {
        return if (shouldReturnNetworkError)
            Resource.error("Error", null)
        else
            Resource.success(
                listOf(CountryResponse(null, null, null, null, null, null, null))
            )
    }

    override fun getAllMessages(): HashMap<Int, List<String>> {
        return hashMapOf()
    }
}