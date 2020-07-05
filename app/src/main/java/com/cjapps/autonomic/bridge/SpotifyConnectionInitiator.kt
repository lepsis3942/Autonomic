package com.cjapps.autonomic.bridge

import android.content.Context
import com.cjapps.utility.coroutines.ICoroutineDispatcherProvider
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class SpotifyConnectionInitiator(
    private val context: Context,
    private val connectionParams: ConnectionParams,
    private val dispatchers: ICoroutineDispatcherProvider
) {

    suspend fun connect(): ConnectionResult {
        return withContext(dispatchers.Main) {
            suspendCoroutine { cont: Continuation<ConnectionResult> ->
                SpotifyAppRemote.connect(
                    context,
                    connectionParams,
                    object : Connector.ConnectionListener {
                        override fun onConnected(appRemote: SpotifyAppRemote?) {
                            Timber.d("Connected to Spotify App Remote")
                            val result = if (appRemote != null) {
                                ConnectionResult.ConnectSuccess(appRemote)
                            } else {
                                Timber.e("Spotify App Remote returned was null!")
                                ConnectionResult.ConnectFailure(NullSpotifyAppRemoteException())
                            }
                            cont.resume(result)
                        }

                        override fun onFailure(error: Throwable?) {
                            Timber.e(error, "Failed to connect to Spotify App Remote")
                            cont.resume(ConnectionResult.ConnectFailure(error))
                        }
                    })
            }
        }
    }

    sealed class ConnectionResult {
        data class ConnectSuccess(val appRemote: SpotifyAppRemote?): ConnectionResult()
        data class ConnectFailure(val error: Throwable?): ConnectionResult()
    }
}