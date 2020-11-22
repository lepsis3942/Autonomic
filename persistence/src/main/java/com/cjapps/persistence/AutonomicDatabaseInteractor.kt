package com.cjapps.persistence

import android.content.Context
import com.cjapps.domain.PlaybackContext
import com.cjapps.domain.Trigger
import com.cjapps.persistence.mapper.toDomain
import com.cjapps.persistence.mapper.toEntity
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class for interacting with Autonomic app persistent data store.  MUST be used as a singleton to
 * avoid recreating the database on each creation
 */
@Singleton
class AutonomicDatabaseInteractor @Inject constructor(applicationContext: Context, runDbInMemory: Boolean = false) {
    private val db by lazy { AutonomicDatabase.getDatabase(applicationContext, runDbInMemory) }

    suspend fun getAllContexts(): List<PlaybackContext> {
        return db.contextDao().getAllContexts().map { it.toDomain() }
    }

    suspend fun insertContext(playbackContext: PlaybackContext) {
        db.contextDao().insertFullContext(playbackContext.toEntity())
    }

    suspend fun deleteContext(playbackContext: PlaybackContext) {
        db.contextDao().deleteContext(playbackContext.toEntity().context)
    }

    suspend fun updateContext(playbackContext: PlaybackContext) {
        db.contextDao().updateFullContext(playbackContext.toEntity())
    }

    suspend fun getAllTriggers(): List<Trigger> {
        return db.triggerDao().getAllTriggers().map { it.toDomain() }
    }

    suspend fun getContextForMacAddress(macAddress: String): PlaybackContext? {
        return db.contextDao().getContextByMacAddress(macAddress)?.toDomain()
    }

    internal suspend fun getNumberPlaylists(): Int {
        return db.playlistDao().getNumberPlaylists()
    }

    internal fun clearDb() {
        db.clearAllTables()
    }
}