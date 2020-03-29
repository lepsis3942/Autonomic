package com.cjapps.autonomic.bridge.di

import com.cjapps.autonomic.bridge.SpotifyPlaybackCommandWorker
import dagger.Module
import dagger.android.ContributesAndroidInjector

/**
 * Created by cjgonz on 2020-03-28.
 */
@Module
abstract class BridgeModule {

    @ContributesAndroidInjector
    abstract fun spotifyCommandWorker(): SpotifyPlaybackCommandWorker
}