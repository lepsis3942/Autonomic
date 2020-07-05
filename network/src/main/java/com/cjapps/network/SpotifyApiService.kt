package com.cjapps.network

import com.cjapps.network.model.Pager
import com.cjapps.network.model.PlaylistSimple
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface SpotifyApiService {

    @JvmSuppressWildcards
    @GET("me/playlists")
    suspend fun getMyPlaylists(@QueryMap optionsMap: Map<String, Any>): Response<Pager<PlaylistSimple>>
}