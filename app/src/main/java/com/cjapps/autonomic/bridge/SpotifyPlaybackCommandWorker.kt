package com.cjapps.autonomic.bridge

import android.Manifest.permission.BLUETOOTH
import android.app.NotificationChannel
import android.app.NotificationManager
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
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
import com.cjapps.persistence.keyvalue.LocalKeyValueDataProvider
import com.cjapps.utility.coroutines.ICoroutineDispatcherProvider
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.SpotifyAppRemote
import com.spotify.protocol.client.CallResult
import com.spotify.protocol.types.Repeat
import dagger.android.HasAndroidInjector
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.coroutines.resume
import com.cjapps.domain.Repeat as DomainRepeat


/**
 * WorkManager worker to initiate playback with the spotify SDK
 * Created by cjgonz on 2020-03-22.
 */
class SpotifyPlaybackCommandWorker @Inject constructor(
    private val context: Context,
    private val parameters: WorkerParameters
) : CoroutineWorker(context, parameters) {
    companion object {
        private const val INPUT_DATA_KEY_PLAYBACK_INFO = "input_data_key_playback_info"
        private const val INPUT_DATA_KEY_MAC_ADDRESS = "input_data_key_mac_address"
        private const val INPUT_DATA_KEY_BLUETOOTH_ACTION = "input_data_key_bluetooth_action"
        private const val NOTIFICATION_ID = 288666642
        private const val NOTIFICATION_CHANNEL_ID = "channel_playback_control"
        private const val SPOTIFY_CONNECTION_TIMEOUT = 20000L
        private const val KEY_LAST_SUCCESSFUL_PLAY = "key_last_successful_play"

        fun buildInputData(
            playbackInfo: PlaybackInfo,
            macAddress: String,
            bluetoothAction: String,
            serializer: ISerializer
        ): Data {
            return Data.Builder()
                .putString(
                    INPUT_DATA_KEY_PLAYBACK_INFO,
                    serializer.toJson(playbackInfo, PlaybackInfo::class)
                )
                .putString(
                    INPUT_DATA_KEY_BLUETOOTH_ACTION,
                    bluetoothAction
                )
                .putString(INPUT_DATA_KEY_MAC_ADDRESS, macAddress)
                .build()
        }
    }

    @Inject
    lateinit var loginRepository: ILoginRepository

    @Inject
    lateinit var serializer: ISerializer

    @Inject
    lateinit var dispatchers: ICoroutineDispatcherProvider

    @Inject
    lateinit var keyValueProvider: LocalKeyValueDataProvider

    init {
        val injector = context.applicationContext as HasAndroidInjector
        injector.androidInjector().inject(this)
    }

    override suspend fun doWork(): Result {
        Timber.d("Launched SpotifyPlaybackCommandWorker")
        var appRemote: SpotifyAppRemote? = null
        try {
            val playbackInfoJson = parameters.inputData.getString(INPUT_DATA_KEY_PLAYBACK_INFO)
                ?: return Result.failure()
            val playbackInfo = serializer.fromJson(playbackInfoJson, PlaybackInfo::class)
                ?: return Result.failure()
            val triggeringAction = parameters.inputData.getString(INPUT_DATA_KEY_BLUETOOTH_ACTION)
                ?: return Result.failure()
            val macAddress = parameters.inputData.getString(INPUT_DATA_KEY_MAC_ADDRESS)
                ?: return Result.failure()

            Timber.d("Bluetooth action is: $triggeringAction")

            setForeground(getForegroundInfo())
            val params = ConnectionParams.Builder(loginRepository.getClientId())
                .showAuthView(false)
                .setRedirectUri(loginRepository.getRedirectUri())
                .build()

            val isConnected = isConnectedToBluetooth(macAddress)
            Timber.d("isConnectedToBluetooth($macAddress):  $isConnected")
            if (triggeringAction == BluetoothDevice.ACTION_ACL_CONNECTED
                && !isConnected
            ) {
                Timber.d("SpotifyPlaybackCommandWorker Ignoring play command.  No longer connected to bluetooth")
                return Result.success()
            }

            Timber.d("Connecting to Spotify")
            val connectionInitiator = SpotifyConnectionInitiator(context, params, dispatchers)

            when (val result = connectionInitiator.connect()) {
                is SpotifyConnectionInitiator.ConnectionResult.ConnectSuccess -> {
                    Timber.d("Successful connection to spotify")
                    appRemote = result.appRemote
                }

                is SpotifyConnectionInitiator.ConnectionResult.ConnectFailure -> {
                    Timber.d("UNSUCCESSFUL connection to spotify")
                    Timber.e(result.error)
                    return Result.failure()
                }
            }

            appRemote?.playerApi?.apply {
                val currentState = playerState?.awaitCallback()

                if (triggeringAction == BluetoothDevice.ACTION_ACL_DISCONNECTED
                    && currentState?.isPaused == false && currentState.track != null
                ) {
                    // Disconnected and playing, stop playback
                    Timber.d("Disconnected from bluetooth and still playing, PAUSING PLAYBACK NOW")
                    pause()?.awaitCallback()
                    return Result.success()
                } else if (triggeringAction == BluetoothDevice.ACTION_ACL_DISCONNECTED) {
                    Timber.d("Disconnected from bluetooth, nothing needed to be done")
                    return Result.success()
                }

                val previousPlayTime = keyValueProvider.getLong(KEY_LAST_SUCCESSFUL_PLAY, -1L)

                if (previousPlayTime == -1L || TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - previousPlayTime) > 60) {
                    playbackInfo.playbackOptions.apply {
                        Timber.d("Setting shuffle")
                        setShuffle(shuffle)?.awaitCallback()
                        val repeat = when (repeat) {
                            DomainRepeat.NONE -> Repeat.OFF
                            DomainRepeat.ALL -> Repeat.ALL
                            DomainRepeat.ONCE -> Repeat.ONE
                        }
                        Timber.d("Setting repeat")
                        setRepeat(repeat)?.awaitCallback()
                    }
                    Timber.d("Launching play command")

                    play(playbackInfo.playbackUri)?.awaitCallback()
                    keyValueProvider.putLong(KEY_LAST_SUCCESSFUL_PLAY, System.currentTimeMillis())
                    Timber.d("Play command finished")
                } else {
                    Timber.d("Skipping Play command due to multiple play commands within 1 minute")
                }
            }
        } finally {
            Timber.d("SpotifyPlaybackCommandWorker running finally block")
            appRemote?.let { SpotifyAppRemote.disconnect(it) }
        }
        Timber.d("SpotifyPlaybackCommandWorker Returning result success")
        return Result.success()
    }

    private suspend inline fun <T> CallResult<T>.awaitCallback(): T {
        return withTimeout(SPOTIFY_CONNECTION_TIMEOUT) {
            suspendCancellableCoroutine { cont ->
                setResultCallback {
                    cont.resume(it)
                }
            }
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
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


        if (Build.VERSION.SDK_INT >= 26 && channel == null) {
            manager.createNotificationChannel(
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.notification_channel_playback_name),
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    private fun isConnectedToBluetooth(macAddress: String): Boolean {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        if (ActivityCompat.checkSelfPermission(
                context,
                BLUETOOTH
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return false
        }
        val bondedDevice =
            bluetoothManager.adapter.bondedDevices.filter { it.address == macAddress }
        return bondedDevice.any()
    }
}