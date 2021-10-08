package com.android.flags.domain

import android.content.Context
import com.android.flags.R
import com.android.flags.domain.model.CountryModel
import com.android.flags.domain.model.TextTemplatesModel
import com.android.flags.remote.CountriesAPI
import com.android.flags.remote.dto.toCountryModel
import com.android.flags.util.Resource
import java.lang.Exception
import javax.inject.Inject

class QuizRepositoryImpl @Inject constructor(
    private val countriesAPI: CountriesAPI,
    private val context: Context
) : QuizRepository {

    override suspend fun getAllCountries(): Resource<List<CountryModel>> {
        return try {
            val response = countriesAPI.getAll()

            if (!response.isSuccessful)
                Resource.error(context.resources.getString(R.string.unknown_error), null)

            response.body()?.let {
                return@let Resource.success(it.filter {
                    it.name?.common != null &&
                            it.capital != null &&
                            it.capital.isNotEmpty() &&
                            it.region != null &&
                            it.flags?.png != null
                }.map {
                    it.toCountryModel()
                })
            } ?: Resource.error(context.resources.getString(R.string.unknown_error), null)
        } catch (e: Exception) {
            Resource.error(context.resources.getString(R.string.unknown_error), null)
        }
    }

    override fun getTextResource(): TextTemplatesModel {
        return TextTemplatesModel(
            context.resources.getStringArray(R.array.greetings).toList(),
            context.resources.getStringArray(R.array.questions).toList(),
            context.resources.getStringArray(R.array.correct_answer_messages).toList(),
            context.resources.getStringArray(R.array.incorrect_answer_messages).toList()
        )
    }
}