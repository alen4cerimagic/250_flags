package com.android.flags.di

import android.content.Context
import android.content.SharedPreferences
import com.android.flags.domain.QuizRepository
import com.android.flags.domain.QuizRepositoryImpl
import com.android.flags.remote.CountriesAPI
import com.android.flags.util.Constants.BASE_URL
import com.android.flags.util.Constants.PREFERENCE_NAME
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideCountriesApi(): CountriesAPI {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(CountriesAPI::class.java)
    }

    @Singleton
    @Provides
    fun provideQuizRepository(
        api: CountriesAPI
    ) = QuizRepositoryImpl(api) as QuizRepository
}