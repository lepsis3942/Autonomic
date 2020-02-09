package com.cjapps.autonomic.network.di

import android.content.Context
import com.cjapps.autonomic.BuildConfig
import com.cjapps.autonomic.R
import com.cjapps.autonomic.network.AutonomicApiService
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

/**
 * Created by cjgonz on 2020-02-09.
 */
@Module
open class AutonomicNetworkModule {
    companion object {
        private const val AUTONOMIC_RETROFIT_NAME = "autonomic_retrofit"
    }

    @Provides
    @Reusable
    @Named(AUTONOMIC_RETROFIT_NAME)
    fun provideRetrofit(applicationContext: Context): Retrofit {
        val okHttpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor(TimberHttpLoggingInterceptor())
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpClient.addInterceptor(httpLoggingInterceptor)
        }

        return Retrofit.Builder()
            .client(okHttpClient.build())
            .baseUrl(applicationContext.getString(R.string.autonomic_base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Reusable
    fun provideAutonomicService(@Named(AUTONOMIC_RETROFIT_NAME) retrofit: Retrofit): AutonomicApiService {
        return retrofit.create(AutonomicApiService::class.java)
    }
}