package com.cjapps.autonomic.di

import com.cjapps.autonomic.serialization.ISerializer
import com.cjapps.autonomic.serialization.MoshiSerializer
import com.cjapps.network.authentication.AuthenticationManager
import com.cjapps.network.authentication.AuthenticationRepository
import com.cjapps.network.authentication.IAuthenticationManager
import com.cjapps.network.authentication.IAuthenticationRepository
import com.cjapps.persistence.keyvalue.IKeyValueDataProvider
import com.cjapps.persistence.keyvalue.LocalKeyValueDataProvider
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
    abstract fun provideDataProvider(localDataProvider: LocalKeyValueDataProvider): IKeyValueDataProvider

    @Binds
    abstract fun provideSerializer(serializer: MoshiSerializer): ISerializer
}