package com.cjapps.autonomic.network

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

/**
 * Created by cjgonz on 2019-09-20.
 */
interface AutonomicApiService {

    @GET
    suspend fun getSpotifyAccessTokenFromCode(
        @Url url: String, // Pass in autonomic service endpoint
        @Query("code") code: String,
        @Query("redirect_uri") redirectUri: String): Response<AuthTokenData>

    @GET
    suspend fun refreshSpotifyToken(
        @Url url: String, // Pass in autonomic service endpoint
        @Query("refresh_token") refreshToken: String): Response<AuthTokenData>
}