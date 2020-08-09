package com.cjapps.autonomic.playback

import com.cjapps.network.SpotifyDataSource
import com.cjapps.network.model.Pager
import com.cjapps.network.model.PlaylistSimple
import com.cjapps.utility.Resource
import dagger.Reusable
import javax.inject.Inject

@Reusable
class PlaybackSelectionRepository @Inject constructor(
    private val spotifyDataSource: SpotifyDataSource
) {

    suspend fun getPlaylists(offset: Int = 0): Resource<Pager<PlaylistSimple>> {
        return spotifyDataSource.getMyPlaylists(offset = offset, limit = 15)
    }
}