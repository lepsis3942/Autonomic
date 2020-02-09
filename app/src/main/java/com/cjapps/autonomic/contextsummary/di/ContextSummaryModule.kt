package com.cjapps.autonomic.contextsummary.di

import androidx.lifecycle.ViewModel
import com.cjapps.autonomic.contextsummary.ContextSummaryFragment
import com.cjapps.autonomic.contextsummary.ContextSummaryViewModel
import com.cjapps.autonomic.di.ViewModelBuilder
import com.cjapps.autonomic.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by cjgonz on 2020-01-26.
 */
@Module
abstract class ContextSummaryModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    abstract fun contextSummaryFragment(): ContextSummaryFragment

    @Binds
    @IntoMap
    @ViewModelKey(ContextSummaryViewModel::class)
    abstract fun bindViewModel(viewModel: ContextSummaryViewModel): ViewModel
}