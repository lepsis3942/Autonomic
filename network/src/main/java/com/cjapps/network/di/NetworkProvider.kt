package com.cjapps.network.di

import android.content.Context
import com.cjapps.network.*
import com.cjapps.network.authentication.IAuthenticationManager
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

internal object NetworkProvider {

    fun provideAutonomicNetworkApi(applicationContext: Context): AutonomicApiService {
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
            .create(AutonomicApiService::class.java)
    }

    fun provideSpotifyNetworkApi(applicationContext: Context, authenticationManager: IAuthenticationManager): SpotifyApiService {
        val okHttpClient = OkHttpClient.Builder()
        okHttpClient.dispatcher(Dispatcher().also { it.maxRequests = 1 })
        okHttpClient.addInterceptor(SpotifyAuthorizationInterceptor(authenticationManager))

        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor(TimberHttpLoggingInterceptor())
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS
            okHttpClient.addInterceptor(httpLoggingInterceptor)
        }

        return Retrofit.Builder()
            .client(okHttpClient.build())
            .baseUrl(applicationContext.getString(R.string.spotify_api_endpoint))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SpotifyApiService::class.java)
    }
}