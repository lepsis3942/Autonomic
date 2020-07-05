package com.cjapps.network.di

import android.content.Context
import com.cjapps.network.AutonomicApiService
import com.cjapps.network.SpotifyApiService
import com.cjapps.network.authentication.AuthenticationManager
import com.cjapps.network.authentication.AuthenticationRepository
import com.cjapps.network.authentication.IAuthenticationManager
import com.cjapps.network.authentication.IAuthenticationRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
abstract class NetworkModule {

    companion object {
        @Provides
        @Reusable
        fun provideAutonomicService(applicationContext: Context): AutonomicApiService {
            return NetworkProvider.provideAutonomicNetworkApi(applicationContext)
        }

        @Provides
        @Reusable
        fun provideSpotifyService(applicationContext: Context, authenticationManager: IAuthenticationManager): SpotifyApiService {
            return NetworkProvider.provideSpotifyNetworkApi(applicationContext, authenticationManager)
        }
    }

    @Binds
    abstract fun provideAuthenticationRepository(authenticationRepo: AuthenticationRepository): IAuthenticationRepository

    @Binds
    abstract fun provideAuthenticationManager(authenticationManager: AuthenticationManager): IAuthenticationManager
}