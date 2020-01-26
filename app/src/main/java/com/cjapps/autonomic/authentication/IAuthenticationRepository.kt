package com.cjapps.autonomic.authentication

import com.cjapps.autonomic.network.AuthTokenData
import com.cjapps.autonomic.network.Resource

/**
 * Created by cjgonz on 2020-01-19.
 */
interface IAuthenticationRepository {
    suspend fun getTokenFromCode(code: String, redirectUrl: String): Resource<AuthTokenData>
    suspend fun refreshAccessToken(refreshToken: String): Resource<AuthTokenData>
}