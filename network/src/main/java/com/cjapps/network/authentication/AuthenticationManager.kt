package com.cjapps.network.authentication

import com.cjapps.network.AuthTokenData
import com.cjapps.network.Resource
import com.cjapps.network.authentication.AuthenticationRepository.Companion.EMPTY_EXPIRES_IN
import com.cjapps.network.isSuccess
import com.cjapps.utility.coroutines.ICoroutineDispatcherProvider
import dagger.Reusable
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject

@Reusable class AuthenticationManager @Inject constructor(
    private val authenticationRepository: IAuthenticationRepository,
    private val dispatchers: ICoroutineDispatcherProvider
) : IAuthenticationManager {
    companion object {
        private const val REFRESH_RETRY_TIMES = 3
        private const val BACKOFF_RETRY_MILLIS = 5000L
        const val EXPIRY_TIME_BUFFER = 30000L // 30 seconds
    }

    @Volatile
    private var cachedCredentials: AuthenticationCredentials? = null

    override suspend fun getAuthInfo(): AuthenticationCredentials {
        val credentials = cachedCredentials
            ?: authenticationRepository.getStoredCredentials()
            ?: throw IllegalStateException("Cannot call getAuthInfo before registering user")

        return if (isValidCredentials(credentials)) {
            credentials
        } else {
            withContext(dispatchers.IO) {
                tryGetRefreshToken(credentials)
            }
        }
    }

    override fun setAuthInfo(authTokenData: AuthTokenData) {
        updateAuthenticationCredentials(authTokenData.toAuthCredentials())
    }

    override fun hasRegistered() = authenticationRepository.hasUserRegistered()

    private suspend fun tryGetRefreshToken(expiredAuthenticationCreds: AuthenticationCredentials): AuthenticationCredentials {
        repeat(REFRESH_RETRY_TIMES) { i ->
            val result = try {
                authenticationRepository.refreshAccessToken(expiredAuthenticationCreds.refreshToken)
            } catch (e: Exception) {
                Resource.error(e.message ?: "", null)
            }
            if (result.isSuccess() && result.data != null) {
                val newAuthenticationCredentials = result.data.toAuthCredentials()
                updateAuthenticationCredentials(newAuthenticationCredentials)
                return@tryGetRefreshToken newAuthenticationCredentials
            }
            delay(BACKOFF_RETRY_MILLIS * i)
        }

        throw Exception("Hit max number of retries to refresh spotify access token")
    }

    private fun updateAuthenticationCredentials(newCreds: AuthenticationCredentials) {
        cachedCredentials = newCreds
        authenticationRepository.storeCredentials(newCreds)
    }

    private fun convertExpiresInToEpochTimestamp(expiresIn: Int): Long {
        // Spotify returns expiry time in seconds
        return Instant.now().toEpochMilli() + (expiresIn * 1000)
    }

    private fun isValidCredentials(authenticationCredentials: AuthenticationCredentials?): Boolean {
        if (authenticationCredentials == null || authenticationCredentials.expiryTimestamp == EMPTY_EXPIRES_IN) return false

        val currentInstant = Instant.now().toEpochMilli()
        return currentInstant < authenticationCredentials.expiryTimestamp - EXPIRY_TIME_BUFFER
    }

    private fun AuthTokenData.toAuthCredentials(): AuthenticationCredentials {
        return AuthenticationCredentials(
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
    suspend fun getAuthInfo(): AuthenticationCredentials
    fun setAuthInfo(authTokenData: AuthTokenData)
    fun hasRegistered(): Boolean
}