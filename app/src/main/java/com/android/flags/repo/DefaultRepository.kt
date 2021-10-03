package com.android.flags.repo

import android.content.Context
import com.android.flags.R
import com.android.flags.data.CountriesAPI
import com.android.flags.data.responses.CountryResponse
import com.android.flags.util.Resource
import java.lang.Exception
import javax.inject.Inject

class DefaultRepository @Inject constructor(
    private val countriesAPI: CountriesAPI,
    private val context: Context
) : MainRepository {
    override suspend fun getAllCountries(): Resource<List<CountryResponse>> {
        return try {
            val response = countriesAPI.getAll()
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let Resource.success(it)
                } ?: Resource.error(context.resources.getString(R.string.unknown_error), null)
            } else
                Resource.error(context.resources.getString(R.string.unknown_error), null)
        } catch (e: Exception) {
            Resource.error(context.resources.getString(R.string.unknown_error), null)
        }
    }

    override fun getAllMessages(): HashMap<Int, List<String>> {
        val result = hashMapOf<Int, List<String>>()
        result[0] = context.resources.getStringArray(R.array.initial_messages).toList()
        result[1] = context.resources.getStringArray(R.array.level1_messages).toList()
        result[2] = context.resources.getStringArray(R.array.level2_messages).toList()
        result[3] = context.resources.getStringArray(R.array.level3_messages).toList()
        result[4] = context.resources.getStringArray(R.array.level4_messages).toList()

        return result
    }
}