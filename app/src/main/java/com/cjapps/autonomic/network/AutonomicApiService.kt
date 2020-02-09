package com.cjapps.autonomic.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by cjgonz on 2019-09-20.
 */
interface AutonomicApiService {

    @GET("authorize")
    suspend fun getSpotifyAccessTokenFromCode(
        @Query("code") code: String,
        @Query("redirect_uri") redirectUri: String): Response<AuthTokenData>

    @GET("refresh_token")
    suspend fun refreshSpotifyToken(
        @Query("refresh_token") refreshToken: String): Response<AuthTokenData>
}