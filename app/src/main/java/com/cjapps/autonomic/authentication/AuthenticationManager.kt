package com.cjapps.autonomic.authentication

import com.cjapps.autonomic.dataprovider.IDataProvider
import com.cjapps.autonomic.network.AuthTokenData
import com.cjapps.autonomic.network.isSuccess
import dagger.Reusable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by cjgonz on 2019-09-14.
 */
@Reusable class AuthenticationManager @Inject constructor(
    private val dataProvider: IDataProvider,
    private val authenticationRepository: IAuthenticationRepository
) : IAuthenticationManager {
    companion object {
        private const val REFRESH_RETRY_TIMES = 3
        private const val BACKOFF_RETRY_MILLIS = 5000L
        const val EXPIRY_TIME_BUFFER = 30000L // 30 seconds
        const val EMPTY_EXPIRES_IN = -1L

        const val KEY_ACCESS_TOKEN = "key_access_token"
        const val KEY_EXPIRES_IN = "key_expires_in"
        const val KEY_REFRESH_TOKEN = "key_refresh_token"
    }

    private var cachedCredentials: AuthCredentials? = null

    override suspend fun getAuthInfo(): AuthCredentials {
        val credentials = cachedCredentials
            ?: getCredentialsFromStorage()
            ?: throw IllegalStateException("Cannot call getAuthInfo before registering user")

        return if (isValidCredentials(credentials)) {
            credentials
        } else {
            withContext(Dispatchers.IO) {
                tryGetRefreshToken(credentials)
            }
        }
    }

    override fun setAuthInfo(authTokenData: AuthTokenData) {
        cachedCredentials = authTokenData.toAuthCreds()

        dataProvider.putString(KEY_ACCESS_TOKEN, authTokenData.accessToken)
        dataProvider.putString(KEY_REFRESH_TOKEN, authTokenData.refreshToken)
        dataProvider.putLong(KEY_EXPIRES_IN, convertExpiresInToEpochTimestamp(authTokenData.expiresIn))
    }

    override fun hasRegistered(): Boolean {
        return !dataProvider.getString(KEY_REFRESH_TOKEN).isNullOrBlank()
    }

    private suspend fun tryGetRefreshToken(expiredAuthCreds: AuthCredentials): AuthCredentials {
        repeat(REFRESH_RETRY_TIMES) { i ->
            val result = authenticationRepository.refreshAccessToken(expiredAuthCreds.refreshToken)
            if (result.isSuccess() && result.data != null) {
                return@tryGetRefreshToken result.data.toAuthCreds()
            }
            delay(BACKOFF_RETRY_MILLIS * i)
        }

        throw Exception("Hit max number of retries to refresh spotify access token")
    }

    private fun convertExpiresInToEpochTimestamp(expiresIn: Int): Long {
        return System.currentTimeMillis() + expiresIn
    }

    private fun isValidCredentials(authCredentials: AuthCredentials?): Boolean {
        if (authCredentials == null || authCredentials.expiryTimestamp == EMPTY_EXPIRES_IN) return false

        val currentInstant = System.currentTimeMillis()
        return (currentInstant + EXPIRY_TIME_BUFFER) > authCredentials.expiryTimestamp
    }

    private fun getCredentialsFromStorage(): AuthCredentials? {
        val accessToken = dataProvider.getString(KEY_ACCESS_TOKEN)
        val refreshToken = dataProvider.getString(KEY_REFRESH_TOKEN)
        val expiresIn = dataProvider.getLong(KEY_EXPIRES_IN, EMPTY_EXPIRES_IN)

        return if (accessToken != null && refreshToken != null && expiresIn != EMPTY_EXPIRES_IN) {
            AuthCredentials(accessToken, refreshToken, expiresIn)
        } else {
            null
        }
    }

    private fun AuthTokenData.toAuthCreds(): AuthCredentials {
        return AuthCredentials(
            accessToken = this.accessToken,
            refreshToken = this.refreshToken,
            expiryTimestamp = convertExpiresInToEpochTimestamp(this.expiresIn)
        )
    }
}

interface IAuthenticationManager {
    /**
     * Must ensure returned Auth info is valid and refreshed if needed
     */
    suspend fun getAuthInfo(): AuthCredentials
    fun setAuthInfo(authTokenData: AuthTokenData)
    fun hasRegistered(): Boolean
}