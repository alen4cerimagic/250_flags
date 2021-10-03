package com.android.flags.di

import android.content.Context
import com.android.flags.R
import com.android.flags.data.CountriesAPI
import com.android.flags.repo.CountriesRepository
import com.android.flags.repo.DefaultRepository
import com.android.flags.util.Constants.BASE_URL
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
    fun provideDefaultRepository(
        api: CountriesAPI
    ) = DefaultRepository(api) as CountriesRepository

    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_image)
            .placeholder(R.drawable.ic_image)
    )
}