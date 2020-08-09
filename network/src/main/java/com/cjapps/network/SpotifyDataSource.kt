package com.cjapps.network

import com.cjapps.network.model.Pager
import com.cjapps.network.model.PlaylistSimple
import com.cjapps.utility.Resource
import javax.inject.Inject

class SpotifyDataSource @Inject constructor(
    private val spotifyApiService: SpotifyApiService
) {
    suspend fun getMyPlaylists(offset: Int = 0, limit: Int = 50): Resource<Pager<PlaylistSimple>> {
        val optionsMap = mapOf(
            SpotifyKeys.PAGER_OPTION_LIMIT to limit,
            SpotifyKeys.PAGER_OPTION_OFFSET to offset
        )

        return spotifyApiService.getMyPlaylists(optionsMap).toResource()
    }
}