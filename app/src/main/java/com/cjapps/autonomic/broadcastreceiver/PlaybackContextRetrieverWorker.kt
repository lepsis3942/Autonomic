package com.cjapps.autonomic.broadcastreceiver

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.cjapps.autonomic.bridge.PlaybackInfo
import com.cjapps.autonomic.bridge.PlaybackOptions
import com.cjapps.autonomic.bridge.SpotifyPlaybackCommandWorker
import com.cjapps.autonomic.serialization.ISerializer
import com.cjapps.persistence.AutonomicDatabaseInteractor
import com.cjapps.utility.coroutines.ICoroutineDispatcherProvider
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject

class PlaybackContextRetrieverWorker @Inject constructor (
    context: Context,
    private val parameters: WorkerParameters
): CoroutineWorker(context, parameters) {
    companion object {
        private const val INPUT_DATA_KEY_MAC_ADDRESS = "input_data_key_mac_address"
        private const val INPUT_DATA_KEY_BLUETOOTH_ACTION = "input_data_key_bluetooth_action"

        fun buildInputData(macAddress: String, bluetoothAction: String): Data {
            return Data.Builder()
                .putString(INPUT_DATA_KEY_MAC_ADDRESS, macAddress)
                .putString(INPUT_DATA_KEY_BLUETOOTH_ACTION, bluetoothAction)
                .build()
        }
    }

    @Inject lateinit var db: AutonomicDatabaseInteractor
    @Inject lateinit var dispatchers: ICoroutineDispatcherProvider
    @Inject lateinit var serializer: ISerializer

    init {
        val injector = context.applicationContext as HasAndroidInjector
        injector.androidInjector().inject(this)
    }

    override suspend fun doWork(): Result {
        val macAddress = parameters.inputData.getString(INPUT_DATA_KEY_MAC_ADDRESS) ?: return Result.failure()
        val bluetoothAction = parameters.inputData.getString(INPUT_DATA_KEY_BLUETOOTH_ACTION)
            ?: return Result.failure()

        Timber.d("Bluetooth action is: $bluetoothAction")

        // No matching trigger found, no need to start spotify worker
        val context = db.getContextForMacAddress(macAddress) ?: return Result.failure()

        val playbackInfo = PlaybackInfo(
            playbackUri = context.playlist.urn,
            playbackOptions = PlaybackOptions(
                shuffle = context.shuffle,
                repeat = context.repeat
            )
        )
        Timber.d("Retrieving PlaybackContext")
        val outputDataForSpotifyWorker = SpotifyPlaybackCommandWorker.buildInputData(
            playbackInfo,
            macAddress,
            bluetoothAction,
            serializer
        )
        return Result.success(outputDataForSpotifyWorker)
    }
}