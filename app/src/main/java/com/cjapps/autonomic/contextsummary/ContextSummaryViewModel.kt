package com.cjapps.autonomic.contextsummary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjapps.network.authentication.IAuthenticationManager
import com.cjapps.utility.livedata.Event
import javax.inject.Inject

/**
 * Created by cjgonz on 2020-01-26.
 */
class ContextSummaryViewModel @Inject constructor(
    private val authenticationManager: IAuthenticationManager
): ViewModel() {

    val navigationEventLiveData by lazy { MutableLiveData<Event<NavDestination>>() }

    fun initialize() {
        if (!authenticationManager.hasRegistered()) {
            navigationEventLiveData.value = Event(Login)
        }
    }

    fun createButtonTapped() {
        navigationEventLiveData.value = Event(ContextCreation)
    }
}

sealed class NavDestination
object Login: NavDestination()
object ContextCreation: NavDestination()