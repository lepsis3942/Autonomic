package com.cjapps.autonomic.bridge

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.PRIORITY_LOW
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import com.cjapps.autonomic.R
import com.cjapps.autonomic.login.ILoginRepository
import com.cjapps.autonomic.serialization.ISerializer
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.CallResult
import dagger.android.HasAndroidInjector
import timber.log.Timber
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * WorkManager worker to initiate playback with the spotify SDK
 * Created by cjgonz on 2020-03-22.
 */
class SpotifyPlaybackCommandWorker @Inject constructor (
    private val context: Context,
    private val parameters: WorkerParameters
): CoroutineWorker(context, parameters) {
    companion object {
        private const val INPUT_DATA_KEY_PLAYBACK_INFO = "input_data_key_playback_info"
        private const val NOTIFICATION_ID = 288666642
        private const val NOTIFICATION_CHANNEL_ID = "channel_playback_control"

        fun buildInputData(playbackInfo: PlaybackInfo, serializer: ISerializer): Data {
            return Data.Builder()
                .putString(INPUT_DATA_KEY_PLAYBACK_INFO, serializer.toJson(playbackInfo, PlaybackInfo::class))
                .build()
        }
    }

    @Inject lateinit var loginRepository: ILoginRepository
    @Inject lateinit var serializer: ISerializer

    init {
        val injector = context.applicationContext as HasAndroidInjector
        injector.androidInjector().inject(this)
    }

    override suspend fun doWork(): Result {
        var appRemote: SpotifyAppRemote? = null
        try {
            val playbackInfoJson = parameters.inputData.getString(INPUT_DATA_KEY_PLAYBACK_INFO)
                ?: return Result.failure()
            val playbackInfo = serializer.fromJson(playbackInfoJson, PlaybackInfo::class)
                ?: return Result.failure()

            setForeground(getForegroundInfo())
            val params = ConnectionParams.Builder(loginRepository.getClientId())
                .showAuthView(false)
                .setRedirectUri(loginRepository.getRedirectUri())
                .build()
            val connectionInitiator = SpotifyConnectionInitiator(context, params)

            when (val result = connectionInitiator.connect()) {
                is SpotifyConnectionInitiator.ConnectionResult.ConnectSuccess -> {
                    appRemote = result.appRemote
                }
                is SpotifyConnectionInitiator.ConnectionResult.ConnectFailure -> {
                    Timber.e(result.error)
                    return Result.failure()
                }
            }

            appRemote?.playerApi?.apply {
                playbackInfo.playbackOptions.apply {
                    setShuffle(shuffle)?.awaitCallback()
                    if (repeatTimes > 0) {
                        setRepeat(repeatTimes)?.awaitCallback()
                    }
                }

                play(playbackInfo.playbackUri)?.awaitCallback()
            }
        } finally {
            appRemote?.let { SpotifyAppRemote.disconnect(it) }
        }

        return Result.success()
    }

    private suspend fun <T> CallResult<T>.awaitCallback(): T {
        return suspendCoroutine {  cont ->
            this.setResultCallback {
                cont.resume(it)
            }
        }
    }

    private fun getForegroundInfo(): ForegroundInfo {
        createChannelIfNeeded()
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
            priority = PRIORITY_LOW
            setContentText(context.getText(R.string.notification_playback_content))
            setSmallIcon(R.mipmap.ic_launcher)
        }.build()
        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    private fun createChannelIfNeeded() {
        val manager = NotificationManagerCompat.from(context)
        val channel = manager.getNotificationChannel(NOTIFICATION_CHANNEL_ID)


        if (channel == null && Build.VERSION.SDK_INT >= 26) {
            manager.createNotificationChannel(NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_channel_playback_name),
                NotificationManager.IMPORTANCE_LOW
            ))
        }
    }
}