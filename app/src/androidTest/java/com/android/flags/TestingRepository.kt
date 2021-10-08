package com.android.flags

import com.android.flags.domain.QuizRepository
import com.android.flags.util.MessageType
import com.android.flags.util.Resource

class TestingRepository : QuizRepository {

    private var shouldReturnNetworkError = false

    override suspend fun getAllCountries(): Resource<List<CountryResponse>> {
        return if (shouldReturnNetworkError)
            Resource.error("Error", null)
        else
            Resource.success(
                listOf(CountryResponse(null, null, null, null, null, null, null))
            )
    }

    override fun getAllMessages(): HashMap<MessageType, List<String>> {
        return hashMapOf()
    }
}