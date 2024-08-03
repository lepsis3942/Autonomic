package com.cjapps.autonomic.broadcastreceiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OverwritingInputMerger
import androidx.work.WorkManager
import com.cjapps.autonomic.bridge.SpotifyPlaybackCommandWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

class BluetoothConnectionReceiver : BroadcastReceiver() {
    companion object {
        const val PLAYBACK_WORK_TAG = "bluetooth_playback_work"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action ?: return
        val macAddress = getMacAddressFromIntent(intent, action)
        if (context == null || macAddress.isNullOrBlank()) return

        Timber.d("BluetoothConnectionReceiver called on receive for macAddress $macAddress")
        Timber.d("Bluetooth action is: $action !!")
        val workerConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setTriggerContentMaxDelay(10, TimeUnit.SECONDS)
            .build()

        val contextRetrieverWorkRequest =
            OneTimeWorkRequestBuilder<PlaybackContextRetrieverWorker>()
                .setInputData(PlaybackContextRetrieverWorker.buildInputData(macAddress, action))
                .setConstraints(workerConstraints)
                .build()
        val spotifyPlaybackWorkRequest = OneTimeWorkRequestBuilder<SpotifyPlaybackCommandWorker>()
            .setInputMerger(OverwritingInputMerger::class.java)
            .setConstraints(workerConstraints)
            .build()

        val workManager = WorkManager.getInstance(context)
        // Pull info matching bluetooth MAC address from DB, cancel work if not found.
        // Then initiate spotify playback with playlist URI
        workManager.cancelAllWorkByTag(PLAYBACK_WORK_TAG)
        workManager
            .beginUniqueWork(
                PLAYBACK_WORK_TAG,
                ExistingWorkPolicy.REPLACE,
                contextRetrieverWorkRequest
            )
            .then(spotifyPlaybackWorkRequest)
            .enqueue()
    }

    private fun getMacAddressFromIntent(intent: Intent?, action: String): String? {
        val connectedDevice =
            intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

        return if (
            (BluetoothDevice.ACTION_ACL_CONNECTED == action || BluetoothDevice.ACTION_ACL_DISCONNECTED == action)
            && connectedDevice != null
        ) {
            connectedDevice.address
        } else {
            null
        }
    }
}