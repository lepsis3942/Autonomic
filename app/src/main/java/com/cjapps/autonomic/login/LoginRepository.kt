package com.cjapps.autonomic.login

import android.content.Context
import com.cjapps.autonomic.R
import com.cjapps.network.AuthTokenData
import com.cjapps.network.authentication.IAuthenticationManager
import com.cjapps.network.authentication.IAuthenticationRepository
import com.cjapps.network.isSuccess
import javax.inject.Inject

/**
 * Created by cjgonz on 2019-09-18.
 */
class LoginRepository @Inject constructor(
    private val applicationContext: Context,
    private val authenticationRepository: IAuthenticationRepository,
    private val authenticationManager: IAuthenticationManager
) : ILoginRepository {

    override fun getAuthorizationScopes(): Array<String> {
        return arrayOf(
            "streaming", "app-remote-control",
            "playlist-read-private", "playlist-read-collaborative", "playlist-modify-public",
            "playlist-modify-private", "user-library-read", "user-library-modify", "user-top-read",
            "user-read-recently-played", "user-read-private"
        )
    }

    override fun getClientId(): String {
        return applicationContext.getString(R.string.spotify_app_client_id)
    }

    override fun getRedirectUri(): String {
        return applicationContext.getString(R.string.spotify_app_callback_url)
    }

    override suspend fun getTokenFromCode(code: String, redirectUrl: String): Boolean {
        val accessTokenInfo = authenticationRepository.getTokenFromCode(code, redirectUrl)
        return if (accessTokenInfo.isSuccess() && accessTokenInfo.data != null) {
            // Now the app can manage the access token and refresh it as needed from here on out
            authenticationManager.setAuthInfo(accessTokenInfo.data as AuthTokenData)
            true
        } else {
            false
        }
    }
}