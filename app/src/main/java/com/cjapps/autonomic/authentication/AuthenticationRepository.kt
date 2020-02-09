package com.cjapps.autonomic.authentication

import com.cjapps.autonomic.network.AuthTokenData
import com.cjapps.autonomic.network.AutonomicApiService
import com.cjapps.autonomic.network.Resource
import com.cjapps.autonomic.network.toResource
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