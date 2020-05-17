package com.cjapps.autonomic.contextdetail.di

import androidx.lifecycle.ViewModel
import com.cjapps.autonomic.contextdetail.ContextDetailFragment
import com.cjapps.autonomic.contextdetail.ContextDetailViewModel
import com.cjapps.autonomic.di.ViewModelBuilder
import com.cjapps.autonomic.di.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap

/**
 * Created by cjgonz on 2020-03-29.
 */
@Module
abstract class ContextDetailModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    abstract fun contextDetailFragment(): ContextDetailFragment

    @Binds
    @IntoMap
    @ViewModelKey(ContextDetailViewModel::class)
    abstract fun bindViewModel(viewModel: ContextDetailViewModel): ViewModel
}