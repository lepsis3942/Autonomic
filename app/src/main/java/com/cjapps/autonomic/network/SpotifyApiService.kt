package com.cjapps.autonomic.network

import com.cjapps.autonomic.domain.spotify.UserPrivate
import retrofit2.Response
import retrofit2.http.GET

/**
 * Created by cjgonz on 2020-01-26.
 */
interface SpotifyApiService {

    @GET("me")
    suspend fun getMe(): Response<UserPrivate>
}