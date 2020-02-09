package com.cjapps.autonomic.network.di

import android.content.Context
import com.cjapps.autonomic.BuildConfig
import com.cjapps.autonomic.R
import com.cjapps.autonomic.authentication.IAuthenticationManager
import com.cjapps.autonomic.network.SpotifyApiService
import com.cjapps.autonomic.network.SpotifyAuthorizationInterceptor
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named

/**
 * Created by cjgonz on 2019-09-20.
 */
@Module
open class SpotifyNetworkModule {
    companion object {
        private const val SPOTIFY_RETROFIT_NAME = "spotify_retrofit"
    }

    @Provides
    @Reusable
    @Named(SPOTIFY_RETROFIT_NAME)
    fun provideRetrofit(applicationContext: Context, authenticationManager: IAuthenticationManager): Retrofit {
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
    }

    @Provides
    @Reusable
    fun provideSpotifyService(@Named(SPOTIFY_RETROFIT_NAME) retrofit: Retrofit): SpotifyApiService {
        return retrofit.create(SpotifyApiService::class.java)
    }
}