package com.cjapps.autonomic.network

import com.cjapps.autonomic.authentication.AuthCredentials
import com.cjapps.autonomic.authentication.IAuthenticationManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by cjgonz on 2020-01-26.
 */
class SpotifyAuthorizationInterceptor constructor(
    private val authenticationManager: IAuthenticationManager
): Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        synchronized(this) {
            var authCredentials: AuthCredentials? = null
            runBlocking {
                authCredentials = authenticationManager.getAuthInfo()
            }

            val authenticatedRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer ${authCredentials?.accessToken}")

            return chain.proceed(authenticatedRequest.build())
        }
    }
}