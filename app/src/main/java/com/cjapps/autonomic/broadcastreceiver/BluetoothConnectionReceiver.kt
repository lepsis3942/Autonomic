package com.cjapps.autonomic.broadcastreceiver

import android.content.Context
import android.content.Intent
import dagger.android.DaggerBroadcastReceiver

/**
 * Created by cjgonz on 2020-03-29.
 */
class BluetoothConnectionReceiver: DaggerBroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (context == null) return
        //TODO: maybe instead schedule this with two workers, one pulling info from the DB, and feeding info to next SpotifyPlaybackWorker if needed

//        WorkManager.getInstance(context)
//            .beginWith( /* Job to pull info matching bluetooth MAC address from DB, cancel work here if no matching mac address found */)
//            .then(SpotifyPlaybackCommandWorker)
//            .enqueue()









//        val playbackInfo = PlaybackInfo(
//            "spotify:playlist:37i9dQZF1DX8CopunbDxgW",
//            PlaybackOptions(shuffle = true)
//        )
//        val request = OneTimeWorkRequestBuilder<SpotifyPlaybackCommandWorker>()
//            .setConstraints(
//                Constraints.Builder()
//                    .setRequiredNetworkType(NetworkType.CONNECTED)
//                    .setTriggerContentMaxDelay(10, TimeUnit.SECONDS)
//                    .build())
//            .setInputData(SpotifyPlaybackCommandWorker.buildInputData(playbackInfo, serializer))
//            .build()
//        context?.let { WorkManager.getInstance(it).enqueue(request) }
    }
}