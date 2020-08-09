package com.cjapps.autonomic.contextdetail

import com.cjapps.domain.PlaybackContext
import com.cjapps.persistence.AutonomicDatabaseInteractor
import com.cjapps.utility.Resource
import com.cjapps.utility.coroutines.ICoroutineDispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ContextDetailRepository @Inject constructor(
    private val dispatchers: ICoroutineDispatcherProvider,
    private val db: AutonomicDatabaseInteractor
) {
    suspend fun saveContext(playbackContext: PlaybackContext): Resource<Unit> {
        // throw error if duplicate trigger used
        return withContext(dispatchers.Default) {
            if (playbackContext.id == 0L) {
                val matchingInUseTrigger = db.getAllTriggers().firstOrNull { it.macAddress == playbackContext.trigger.macAddress }
                if (matchingInUseTrigger == null) {
                    db.insertContext(playbackContext)
                    Resource.success(Unit)
                } else {
                    Resource.error("Duplicate trigger", Unit)
                }
            } else {
                db.updateContext(playbackContext)
                Resource.success(Unit)
            }
        }
    }
}