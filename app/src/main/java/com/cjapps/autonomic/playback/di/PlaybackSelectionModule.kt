package com.cjapps.autonomic.playback.di

import androidx.lifecycle.ViewModel
import com.cjapps.autonomic.di.ViewModelBuilder
import com.cjapps.autonomic.di.ViewModelKey
import com.cjapps.autonomic.playback.PlaybackSelectionFragment
import com.cjapps.autonomic.playback.PlaybackSelectionViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

@Module
abstract class PlaybackSelectionModule {
    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    abstract fun playbackSelectionFragment(): PlaybackSelectionFragment

    @Binds
    @IntoMap
    @ViewModelKey(PlaybackSelectionViewModel::class)
    abstract fun bindViewModel(viewModel: PlaybackSelectionViewModel): ViewModel
}