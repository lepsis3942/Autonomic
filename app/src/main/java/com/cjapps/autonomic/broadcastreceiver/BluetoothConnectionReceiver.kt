package com.cjapps.autonomic.broadcastreceiver

import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.cjapps.autonomic.bridge.SpotifyPlaybackCommandWorker
import java.util.concurrent.TimeUnit

class BluetoothConnectionReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        val macAddress = getMacAddressFromIntent(intent, intent?.action)
        if (context == null || macAddress.isNullOrBlank()) return

        val workerConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setTriggerContentMaxDelay(10, TimeUnit.SECONDS)
            .build()

        val contextRetrieverWorkRequest = OneTimeWorkRequestBuilder<PlaybackContextRetrieverWorker>()
            .setInputData(PlaybackContextRetrieverWorker.buildInputData(macAddress))
            .setConstraints(workerConstraints)
            .build()
        val spotifyPlaybackWorkRequest = OneTimeWorkRequestBuilder<SpotifyPlaybackCommandWorker>()
            .setInputMerger(OverwritingInputMerger::class.java)
            .setConstraints(workerConstraints)
            .build()

        // Pull info matching bluetooth MAC address from DB, cancel work if not found.
        // Then initiate spotify playback with playlist URI
        WorkManager.getInstance(context)
            .beginWith(contextRetrieverWorkRequest)
            .then(spotifyPlaybackWorkRequest)
            .enqueue()
    }

    private fun getMacAddressFromIntent(intent: Intent?, action: String?): String? {
        val connectedDevice = intent?.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

        return if (BluetoothDevice.ACTION_ACL_CONNECTED == action && connectedDevice != null) {
            connectedDevice.address
        } else {
            null
        }
    }
}