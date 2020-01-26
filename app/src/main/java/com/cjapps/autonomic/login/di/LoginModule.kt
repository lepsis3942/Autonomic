package com.cjapps.autonomic.login.di

import androidx.lifecycle.ViewModel
import com.cjapps.autonomic.di.ViewModelBuilder
import com.cjapps.autonomic.di.ViewModelKey
import com.cjapps.autonomic.login.ILoginRepository
import com.cjapps.autonomic.login.LoginFragment
import com.cjapps.autonomic.login.LoginRepository
import com.cjapps.autonomic.login.LoginViewModel
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import dagger.multibindings.IntoMap
import javax.inject.Singleton

/**
 * Created by cjgonz on 2020-01-05.
 */
@Module
abstract class LoginModule {

    @ContributesAndroidInjector(modules = [
        ViewModelBuilder::class
    ])
    abstract fun loginFragment(): LoginFragment

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    abstract fun bindViewModel(viewModel: LoginViewModel): ViewModel

    @Singleton
    @Binds
    abstract fun provideLoginRepository(loginRepository: LoginRepository): ILoginRepository
}