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
import timber.log.Timber

/**
 * Created by cjgonz on 2019-09-20.
 */
@Module
open class NetworkModule {

    @Provides
    @Reusable
    fun provideRetrofit(applicationContext: Context): Retrofit {
        val httpLoggingInterceptor = HttpLoggingInterceptor(
            object: HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Timber.d(message)
                }
            }
        )
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            okHttpClient.addInterceptor(httpLoggingInterceptor)
        }

        return Retrofit.Builder()
            .client(okHttpClient.build())
            .baseUrl(applicationContext.getString(R.string.spotify_api_endpoint))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Reusable
    fun provideAutonomicService(retrofit: Retrofit): AutonomicApiService {
        return retrofit.create(AutonomicApiService::class.java)
    }
}