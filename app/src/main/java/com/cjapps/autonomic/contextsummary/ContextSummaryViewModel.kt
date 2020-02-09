package com.cjapps.autonomic.contextsummary

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.autonomic.authentication.IAuthenticationManager
import com.cjapps.autonomic.livedata.Event
import com.cjapps.autonomic.network.SpotifyApiService
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by cjgonz on 2020-01-26.
 */
class ContextSummaryViewModel @Inject constructor(
    private val authenticationManager: IAuthenticationManager,
    private val spotifyApiService: SpotifyApiService
): ViewModel() {

    val navigationEventLiveData by lazy { MutableLiveData<Event<NavDestination>>() }

    fun initialize() {
        if (!authenticationManager.hasRegistered()) {
            navigationEventLiveData.value = Event(Login)
        }
    }

    fun test() {
        viewModelScope.launch {
            val x = spotifyApiService.getMe()
            Timber.d(x.body().toString())
        }
    }
}

sealed class NavDestination
object Login: NavDestination()