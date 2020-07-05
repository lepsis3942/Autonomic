package com.cjapps.autonomic.playback

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.network.isSuccess
import com.cjapps.network.model.PlaylistSimple
import com.cjapps.utility.coroutines.ICoroutineDispatcherProvider
import com.cjapps.utility.livedata.Event
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min

class PlaybackSelectionViewModel @Inject constructor(
    private val repository: PlaybackSelectionRepository,
    private val dispatchers: ICoroutineDispatcherProvider
) : ViewModel() {
    private var allPlaylistsRetrieved = false
    private var currentPlaylistPagerOffset = 0
    private var isInitialized = false
    private val navUiStateLiveData = MutableLiveData<Event<NavEvent>>()
    private val playlistUiStateLiveData = MutableLiveData<PlaylistUiState>()
    private val loadingUiStateLiveData = MutableLiveData<LoadingUiState>()
    private val playlists = mutableListOf<PlaylistSimple>()

    val viewSate = UiState(
        playlistUi = playlistUiStateLiveData,
        loadingUiState = loadingUiStateLiveData,
        navEvent = navUiStateLiveData
    )

    fun executeAction(action: PlaybackSelectionAction) {
        when (action) {
            PlaybackSelectionAction.Init -> {
                if (!isInitialized) {
                    isInitialized = true
                    loadingUiStateLiveData.value = LoadingUiState.LoadingInitial
                    getPlaylists(offset = currentPlaylistPagerOffset, initialLoad = true)
                }
            }
            PlaybackSelectionAction.LoadMorePlaylists -> {
                if (!allPlaylistsRetrieved) getPlaylists(offset = currentPlaylistPagerOffset)
            }
            is PlaybackSelectionAction.PlaylistSelected -> {
                navUiStateLiveData.value = Event(NavEvent.NavigateBack(action.playlist))
            }
        }
    }

    private fun getPlaylists(offset: Int = 0, initialLoad: Boolean = false) {
        if (!initialLoad) {
            loadingUiStateLiveData.value = LoadingUiState.LoadingMorePlaylists
        }
        viewModelScope.launch(dispatchers.Default) {
            val playlistResource = repository.getPlaylists(offset)
            if (playlistResource.isSuccess() && playlistResource.data != null) {
                playlistResource.data?.apply {
                    allPlaylistsRetrieved = next == null
                    items?.let { playlists.addAll(it) }
                    currentPlaylistPagerOffset = min(total, currentPlaylistPagerOffset + limit)
                    playlistUiStateLiveData.postValue(
                        PlaylistUiState.PlaylistsRetrieved(
                            allPlaylists = playlists.toList(),
                            endlessScrollingEnabled = currentPlaylistPagerOffset < this.total
                        )
                    )
                    loadingUiStateLiveData.postValue(LoadingUiState.None)
                }
            }
        }
    }

    data class UiState(
        val playlistUi: LiveData<PlaylistUiState>,
        val loadingUiState: LiveData<LoadingUiState>,
        val navEvent: LiveData<Event<NavEvent>>
    )
    sealed class PlaylistUiState {
        data class PlaylistsRetrieved(
            val allPlaylists: List<PlaylistSimple>,
            val endlessScrollingEnabled: Boolean
        ) : PlaylistUiState()
    }
    sealed class LoadingUiState {
        object LoadingInitial : LoadingUiState()
        object LoadingMorePlaylists : LoadingUiState()
        object None : LoadingUiState()
    }
}

sealed class PlaybackSelectionAction {
    object Init : PlaybackSelectionAction()
    object LoadMorePlaylists : PlaybackSelectionAction()
    data class PlaylistSelected(val playlist: PlaylistSimple) : PlaybackSelectionAction()
}

sealed class NavEvent {
    data class NavigateBack(val playlist: PlaylistSimple) : NavEvent()
}