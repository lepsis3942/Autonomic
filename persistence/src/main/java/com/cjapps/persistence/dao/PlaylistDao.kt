package com.cjapps.persistence.dao

import androidx.room.*
import com.cjapps.persistence.AutonomicDatabase
import com.cjapps.persistence.entity.Playlist
import com.cjapps.persistence.entity.PlaylistWithImages

@Dao
internal abstract class PlaylistDao (private val db: AutonomicDatabase) {
    @Query("SELECT * FROM PLAYLIST")
    abstract suspend fun getAllPlaylists(): List<Playlist>

    @Insert
    abstract suspend fun insertPlaylist(playlist: Playlist): Long

    @Update
    abstract suspend fun updatePlaylist(playlist: Playlist)

    @Query("SELECT COUNT(id) FROM PLAYLIST")
    abstract suspend fun getNumberPlaylists(): Int

    @Transaction
    open suspend fun insertPlaylistWithImages(playlistWithImages: PlaylistWithImages) {
        val playlistId = insertPlaylist(playlistWithImages.playlist)

        playlistWithImages.images.forEach {
            it.playlistId = playlistId
            db.imageDao().insertImage(it)
        }
    }

    @Transaction
    open suspend fun updatePlaylistWithImages(playlistWithImages: PlaylistWithImages) {
        updatePlaylist(playlistWithImages.playlist)
        db.imageDao().updateImages(playlistWithImages.images)
    }
}