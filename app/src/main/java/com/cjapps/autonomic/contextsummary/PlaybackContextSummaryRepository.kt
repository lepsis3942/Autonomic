package com.cjapps.autonomic.contextsummary

import com.cjapps.domain.PlaybackContext
import com.cjapps.persistence.AutonomicDatabaseInteractor
import com.cjapps.utility.coroutines.ICoroutineDispatcherProvider
import kotlinx.coroutines.withContext
import javax.inject.Inject

class PlaybackContextSummaryRepository @Inject constructor (
    private val dispatchers: ICoroutineDispatcherProvider,
    private val db: AutonomicDatabaseInteractor
) {
    suspend fun getContexts(): List<PlaybackContext> {
        return withContext(dispatchers.IO) {
            db.getAllContexts()
        }
    }
}