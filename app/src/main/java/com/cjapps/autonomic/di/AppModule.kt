package com.cjapps.autonomic.di

import com.cjapps.autonomic.serialization.ISerializer
import com.cjapps.autonomic.serialization.MoshiSerializer
import com.cjapps.persistence.keyvalue.IKeyValueDataProvider
import com.cjapps.persistence.keyvalue.LocalKeyValueDataProvider
import com.cjapps.utility.coroutines.CoroutineDispatcherProvider
import com.cjapps.utility.coroutines.ICoroutineDispatcherProvider
import dagger.Binds
import dagger.Module
import dagger.Reusable

/**
 * Created by cjgonz on 2019-09-14.
 */
@Module
abstract class AppModule {
    @Binds
    abstract fun provideDataProvider(localDataProvider: LocalKeyValueDataProvider): IKeyValueDataProvider

    @Binds
    abstract fun provideSerializer(serializer: MoshiSerializer): ISerializer

    @Binds
    @Reusable
    abstract fun provideCoroutineDispatcher(coroutineDispatcherProvider: CoroutineDispatcherProvider): ICoroutineDispatcherProvider
}