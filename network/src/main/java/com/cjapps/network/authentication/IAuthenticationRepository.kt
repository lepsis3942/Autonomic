package com.cjapps.network.authentication

import com.cjapps.network.AuthTokenData
import com.cjapps.network.Resource

/**
 * Created by cjgonz on 2020-01-19.
 */
interface IAuthenticationRepository {
    suspend fun getTokenFromCode(code: String, redirectUrl: String): Resource<AuthTokenData>
    suspend fun refreshAccessToken(refreshToken: String): Resource<AuthTokenData>
}