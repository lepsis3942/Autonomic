package com.cjapps.autonomic.di

import android.content.Context
import com.cjapps.autonomic.serialization.ISerializer
import com.cjapps.autonomic.serialization.MoshiSerializer
import com.cjapps.persistence.AutonomicDatabaseInteractor
import com.cjapps.persistence.keyvalue.IKeyValueDataProvider
import com.cjapps.persistence.keyvalue.LocalKeyValueDataProvider
import com.cjapps.utility.coroutines.CoroutineDispatcherProvider
import com.cjapps.utility.coroutines.ICoroutineDispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
abstract class AppModule {
    companion object {
        @Provides
        @Reusable
        fun providesDbInteractor(applicationContext: Context): AutonomicDatabaseInteractor {
            return AutonomicDatabaseInteractor(applicationContext, runDbInMemory = false)
        }
     }

    @Binds
    abstract fun provideDataProvider(localDataProvider: LocalKeyValueDataProvider): IKeyValueDataProvider

    @Binds
    abstract fun provideSerializer(serializer: MoshiSerializer): ISerializer

    @Binds
    @Reusable
    abstract fun provideCoroutineDispatcher(coroutineDispatcherProvider: CoroutineDispatcherProvider): ICoroutineDispatcherProvider
}