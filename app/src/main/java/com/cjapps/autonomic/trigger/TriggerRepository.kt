package com.cjapps.autonomic.trigger

import com.cjapps.persistence.AutonomicDatabaseInteractor
import dagger.Reusable
import javax.inject.Inject

@Reusable
class TriggerRepository @Inject constructor(
    private val db: AutonomicDatabaseInteractor
) {
    suspend fun getUnavailableTriggerMacAddresses(): HashSet<String> {
        return db.getAllTriggers()
            .map { trigger -> trigger.macAddress }
            .toHashSet()
    }
}