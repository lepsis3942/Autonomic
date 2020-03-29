package com.cjapps.autonomic.di

import android.content.Context
import com.cjapps.autonomic.AutonomicApplication
import com.cjapps.autonomic.bridge.di.BridgeModule
import com.cjapps.autonomic.contextsummary.di.ContextSummaryModule
import com.cjapps.autonomic.login.di.LoginModule
import com.cjapps.autonomic.network.di.AutonomicNetworkModule
import com.cjapps.autonomic.network.di.SpotifyNetworkModule
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

/**
 * Created by cjgonz on 2019-09-14.
 */
@Component(modules = [
    AndroidSupportInjectionModule::class,
    AppModule::class,
    SpotifyNetworkModule::class,
    AutonomicNetworkModule::class,
    LoginModule::class,
    ContextSummaryModule::class,
    BridgeModule::class
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
