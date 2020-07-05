package com.cjapps.network

import com.cjapps.network.authentication.AuthenticationCredentials
import com.cjapps.network.authentication.IAuthenticationManager
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException

/**
 * Created by cjgonz on 2020-01-26.
 */
internal class SpotifyAuthorizationInterceptor constructor(
    private val authenticationManager: IAuthenticationManager
) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        synchronized(this) {
            var authenticationCredentials: AuthenticationCredentials?
            runBlocking {
                authenticationCredentials = try {
                    authenticationManager.getAuthInfo()
                } catch (ex: Exception) {
                    Timber.e(ex)
                    AuthenticationCredentials("empty", "empty", -1)
                }
            }

            val authenticatedRequest = originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer ${authenticationCredentials?.accessToken}")

            return chain.proceed(authenticatedRequest.build())
        }
    }
}