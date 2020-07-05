package com.cjapps.network.authentication

import com.cjapps.network.AuthTokenData
import com.cjapps.network.Resource

interface IAuthenticationRepository {
    suspend fun getTokenFromCode(code: String, redirectUrl: String) : Resource<AuthTokenData>
    suspend fun refreshAccessToken(refreshToken: String) : Resource<AuthTokenData>
    fun getStoredCredentials(): AuthenticationCredentials?
    fun storeCredentials(authenticationCredentials: AuthenticationCredentials)
    fun hasUserRegistered(): Boolean
}