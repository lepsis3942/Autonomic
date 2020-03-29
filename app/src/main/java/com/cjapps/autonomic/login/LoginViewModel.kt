package com.cjapps.autonomic.login

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.autonomic.R
import com.cjapps.autonomic.livedata.Event
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by cjgonz on 2019-09-14.
 */
class LoginViewModel @Inject constructor(
    private val applicationContext: Context,
    private val loginRepository: ILoginRepository
) : ViewModel() {
    val isLoadingLiveData by lazy { MutableLiveData<Boolean>(false) }
    val launchAuthenticationLiveData by lazy { MutableLiveData<Event<AuthenticationRequest>>() }
    val errorEventLiveData by lazy { MutableLiveData<Event<String>>() }
    val snackBarEventLiveData by lazy { MutableLiveData<Event<String>>() }
    val loginCompleteEventLiveData by lazy { MutableLiveData<Event<Unit>>() }

    fun loginButtonClicked() {
        isLoadingLiveData.value = true

        val authRequestBuilder = AuthenticationRequest.Builder(
            loginRepository.getClientId(),
            AuthenticationResponse.Type.CODE,
            loginRepository.getRedirectUri()
        )
        authRequestBuilder.setScopes(loginRepository.getAuthorizationScopes())
        launchAuthenticationLiveData.value = Event(authRequestBuilder.build())
    }

    fun handleAuthenticationResponse(intent: Intent?, resultCode: Int) {
        val authResponse = AuthenticationClient.getResponse(resultCode, intent)
        when (authResponse.type) {
            AuthenticationResponse.Type.CODE -> {
                viewModelScope.launch {
                    val successfulLogin = loginRepository.getTokenFromCode(
                        authResponse.code,
                        applicationContext.getString(R.string.spotify_app_callback_url))

                    if (successfulLogin) {
                        loginCompleteEventLiveData.value = Event(Unit)
                    } else {
                        errorEventLiveData.value
                    }
                    isLoadingLiveData.value = false
                }
            }
            AuthenticationResponse.Type.ERROR -> {
                errorEventLiveData.value = Event("Error Authentication Response")
            }
            else -> {
                // Most likely user cancelled auth flow
                isLoadingLiveData.value = false
            }
        }
    }
}