package com.cjapps.persistence.dao

import androidx.room.*
import com.cjapps.persistence.AutonomicDatabase
import com.cjapps.persistence.entity.Context
import com.cjapps.persistence.entity.FullContext

@Dao
internal abstract class ContextDao constructor(private val db: AutonomicDatabase) {
    @Transaction
    @Query("SELECT * FROM CONTEXT")
    abstract suspend fun getAllContexts(): List<FullContext>

    @Transaction
    @Delete
    abstract suspend fun deleteContext(context: Context)

    @Insert
    abstract suspend fun insertContext(context: Context): Long

    @Update
    abstract suspend fun updateContext(context: Context)

    @Transaction
    open suspend fun insertFullContext(fullContext: FullContext) {
        val contextId = insertContext(fullContext.context)

        fullContext.trigger.contextId = contextId
        db.triggerDao().insertTrigger(fullContext.trigger)

        fullContext.playlistAndImages.apply {
            playlist.contextId = contextId
            db.playlistDao().insertPlaylistWithImages(this)
        }
    }

    @Transaction
    open suspend fun updateFullContext(fullContext: FullContext) {
        updateContext(fullContext.context)
        db.playlistDao().updatePlaylistWithImages(fullContext.playlistAndImages)
        db.triggerDao().updateTrigger(fullContext.trigger)
    }
}