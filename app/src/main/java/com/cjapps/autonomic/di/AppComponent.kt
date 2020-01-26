package com.cjapps.autonomic.di

import android.content.Context
import com.cjapps.autonomic.AutonomicApplication
import com.cjapps.autonomic.login.di.LoginModule
import com.cjapps.autonomic.network.di.NetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by cjgonz on 2019-09-14.
 */
@Component(modules = [
    AndroidSupportInjectionModule::class,
    LoginModule::class,
    AppModule::class,
    NetworkModule::class
])
@Singleton
interface AppComponent {

    @Component.Builder
    interface Builder {
        @BindsInstance fun applicationContext(applicationContext: Context): Builder
        fun build(): AppComponent
    }

    fun inject(application: AutonomicApplication)
}
