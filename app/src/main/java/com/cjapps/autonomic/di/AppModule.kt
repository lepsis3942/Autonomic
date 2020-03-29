package com.cjapps.autonomic.di

import com.cjapps.autonomic.authentication.AuthenticationManager
import com.cjapps.autonomic.authentication.AuthenticationRepository
import com.cjapps.autonomic.authentication.IAuthenticationManager
import com.cjapps.autonomic.authentication.IAuthenticationRepository
import com.cjapps.autonomic.dataprovider.IDataProvider
import com.cjapps.autonomic.dataprovider.LocalDataProvider
import com.cjapps.autonomic.serialization.ISerializer
import com.cjapps.autonomic.serialization.MoshiSerializer
import dagger.Binds
import dagger.Module

/**
 * Created by cjgonz on 2019-09-14.
 */
@Module
abstract class AppModule {
    @Binds
    abstract fun provideAuthenticationRepository(authenticationRepo: AuthenticationRepository): IAuthenticationRepository

    @Binds
    abstract fun provideAuthenticationManager(authenticationManager: AuthenticationManager): IAuthenticationManager

    @Binds
    abstract fun provideDataProvider(localDataProvider: LocalDataProvider): IDataProvider

    @Binds
    abstract fun provideSerializer(serializer: MoshiSerializer): ISerializer
}