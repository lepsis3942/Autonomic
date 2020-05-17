package com.cjapps.persistence.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.cjapps.persistence.entity.Trigger

@Dao
internal interface TriggerDao {
    @Query("SELECT * FROM `TRIGGER`")
    suspend fun getAllTriggers(): List<Trigger>

    @Insert
    suspend fun insertTrigger(trigger: Trigger): Long

    @Update
    suspend fun updateTrigger(trigger: Trigger)
}