package com.cjapps.autonomic.trigger.di

import androidx.lifecycle.ViewModel
import com.cjapps.autonomic.di.ViewModelBuilder
import com.cjapps.autonomic.di.ViewModelKey
import com.cjapps.autonomic.trigger.TriggerSelectionFragment
import com.cjapps.autonomic.trigger.TriggerSelectionViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by cjgonz on 4/11/20.
 */
@Module
abstract class TriggerSelectionModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    abstract fun triggerSelectionFragment(): TriggerSelectionFragment

    @Binds
    @IntoMap
    @ViewModelKey(TriggerSelectionViewModel::class)
    abstract fun bindViewModel(viewModel: TriggerSelectionViewModel): ViewModel
}