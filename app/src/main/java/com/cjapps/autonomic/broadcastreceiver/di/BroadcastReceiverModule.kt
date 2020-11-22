package com.cjapps.autonomic.broadcastreceiver.di

import com.cjapps.autonomic.broadcastreceiver.PlaybackContextRetrieverWorker
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class BroadcastReceiverModule {

    @ContributesAndroidInjector
    abstract fun playbackContextRetriever(): PlaybackContextRetrieverWorker
}