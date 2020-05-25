package com.cjapps.network.authentication

import com.cjapps.network.AuthTokenData
import com.cjapps.network.AutonomicApiService
import com.cjapps.network.Resource
import com.cjapps.network.toResource
import javax.inject.Inject

/**
 * Created by cjgonz on 2020-01-19.
 */
class AuthenticationRepository @Inject constructor(
    private val autonomicApiService: AutonomicApiService
): IAuthenticationRepository {

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
}