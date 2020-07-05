package com.cjapps.network.authentication

import com.cjapps.network.AuthTokenData
import com.cjapps.network.AutonomicApiService
import com.cjapps.network.Resource
import com.cjapps.network.toResource
import com.cjapps.persistence.keyvalue.IKeyValueDataProvider
import javax.inject.Inject

/**
 * Created by cjgonz on 2020-01-19.
 */
class AuthenticationRepository @Inject constructor(
    private val autonomicApiService: AutonomicApiService,
    private val dataProvider: IKeyValueDataProvider
) : IAuthenticationRepository {
    companion object {
        const val EMPTY_EXPIRES_IN = -1L

        const val KEY_ACCESS_TOKEN = "key_access_token"
        const val KEY_EXPIRY_TIMESTAMP = "key_expiry_timestamp"
        const val KEY_REFRESH_TOKEN = "key_refresh_token"
    }

    override suspend fun getTokenFromCode(
        code: String,
        redirectUrl: String)
            : Resource<AuthTokenData> {
        return autonomicApiService
            .getSpotifyAccessTokenFromCode(code, redirectUrl)
            .toResource()
    }

    /**
     * Use refresh token to get a new, valid access token
     */
    override suspend fun refreshAccessToken(refreshToken: String): Resource<AuthTokenData> {
        return autonomicApiService
            .refreshSpotifyToken(refreshToken)
            .toResource()
    }

    override fun getStoredCredentials(): AuthenticationCredentials? {
        val accessToken = dataProvider.getString(KEY_ACCESS_TOKEN)
        val refreshToken = dataProvider.getString(KEY_REFRESH_TOKEN)
        val expiresIn = dataProvider.getLong(KEY_EXPIRY_TIMESTAMP, EMPTY_EXPIRES_IN)

        return if (accessToken != null && refreshToken != null && expiresIn != EMPTY_EXPIRES_IN) {
            AuthenticationCredentials(accessToken, refreshToken, expiresIn)
        } else {
            null
        }
    }

    override fun storeCredentials(authenticationCredentials: AuthenticationCredentials) {
        dataProvider.putString(KEY_ACCESS_TOKEN, authenticationCredentials.accessToken)
        dataProvider.putString(KEY_REFRESH_TOKEN, authenticationCredentials.refreshToken)
        dataProvider.putLong(KEY_EXPIRY_TIMESTAMP, authenticationCredentials.expiryTimestamp)
    }

    override fun hasUserRegistered(): Boolean {
        return !dataProvider.getString(KEY_REFRESH_TOKEN).isNullOrBlank()
    }
}