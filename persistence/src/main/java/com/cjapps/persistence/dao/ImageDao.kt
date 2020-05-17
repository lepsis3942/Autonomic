package com.cjapps.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cjapps.persistence.entity.Image

@Dao
internal interface ImageDao {
    @Query("SELECT * FROM IMAGE")
    suspend fun getAllImages(): List<Image>

    @Insert
    suspend fun insertImage(images: Image): Long

    @Update
    suspend fun updateImages(images: List<Image>)
}