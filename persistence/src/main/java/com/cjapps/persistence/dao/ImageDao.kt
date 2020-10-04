package com.cjapps.persistence.dao

import androidx.room.*
import com.cjapps.persistence.entity.Image

@Dao
internal interface ImageDao {
    @Query("SELECT * FROM IMAGE")
    suspend fun getAllImages(): List<Image>

    @Query("SELECT * FROM IMAGE WHERE PLAYLIST_ID = :playlistId")
    suspend fun getImagesForPlaylist(playlistId: Long): List<Image>

    @Insert
    suspend fun insertImage(image: Image): Long

    @Update
    suspend fun updateImages(images: List<Image>)

    @Delete
    suspend fun deleteImage(image: Image)
}