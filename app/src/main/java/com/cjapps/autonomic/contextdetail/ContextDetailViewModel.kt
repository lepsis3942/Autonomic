package com.cjapps.autonomic.contextdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.domain.PlaybackContext
import com.cjapps.domain.Playlist
import com.cjapps.domain.Trigger
import com.cjapps.network.isSuccess
import com.cjapps.utility.livedata.Event
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContextDetailViewModel @Inject constructor(
    private val repository: ContextDetailRepository
) : ViewModel() {
    private val errorEvent = MutableLiveData<Event<String>>()
    private val playbackUiState = MutableLiveData<PlaybackUiState>()
    private val triggerUiState = MutableLiveData<TriggerUiState>()
    private val navEvent = MutableLiveData<Event<NavEvent>>()
    private var isUiDirty = false
    private var isInitialized = false
    private var contextId: Long = 0L
    private var currentlySelectedTrigger: Trigger? = null
    private var currentlySelectedPlayback: Playlist? = null

    val viewState = UiState(
        errorEvent,
        playbackUiState,
        triggerUiState,
        navEvent
    )

    fun executeAction(action: ContextDetailAction) {
        when (action) {
            is ContextDetailAction.Init -> initialize(action.contextToEdit)
            is ContextDetailAction.ChooseMusic -> navEvent.value = Event(NavEvent.SetMusic)
            is ContextDetailAction.ChooseTrigger -> navEvent.value = Event(NavEvent.SetTrigger)
            is ContextDetailAction.TriggerUpdated -> handleTriggerUpdate(action.newTrigger)
            is ContextDetailAction.PlaybackUpdated -> handlePlaybackUpdated(action.playlist)
            is ContextDetailAction.Save -> {
                val playlist = currentlySelectedPlayback
                val trigger = currentlySelectedTrigger
                if (playlist != null && trigger != null) {
                    handleSave(playlist, trigger)
                } else {
                    errorEvent.value = Event("Please select both a playlist and trigger")
                }
            }
        }
    }

    private fun initialize(contextToEdit: PlaybackContext?) {
        if (isInitialized) return

        if (contextToEdit != null) {
            // Edit mode
            contextId = contextToEdit.id
            contextToEdit.trigger.apply {
                currentlySelectedTrigger = this
                triggerUiState.value = TriggerUiState.CanEdit(name)
            }
            contextToEdit.playlist.apply {
                currentlySelectedPlayback = this
                playbackUiState.value = PlaybackUiState.CanEdit(title, images.firstOrNull()?.url)
            }
        } else {
            triggerUiState.value = TriggerUiState.Unset
            playbackUiState.value = PlaybackUiState.Unset
        }
        isInitialized = true
    }

    private fun handleTriggerUpdate(newTrigger: Trigger) {
        val currentTrigger = currentlySelectedTrigger
        if (currentTrigger != null && currentTrigger.macAddress == newTrigger.macAddress) return

        newTrigger.id = currentTrigger?.id ?: 0L
        isUiDirty = true
        currentlySelectedTrigger = newTrigger
        currentlySelectedTrigger?.let { triggerUiState.value = TriggerUiState.CanEdit(it.name) }
    }

    private fun handlePlaybackUpdated(playlist: Playlist) {
        val currentPlayback = currentlySelectedPlayback
        if (currentPlayback != null && currentPlayback.urn == playlist.urn) return

        playlist.id = currentPlayback?.id ?: 0L
        isUiDirty = true
        currentlySelectedPlayback = playlist
        currentlySelectedPlayback?.let { playbackUiState.value = PlaybackUiState.CanEdit(it.title, it.images.firstOrNull()?.url) }
    }

    private fun handleSave(playlist: Playlist, trigger: Trigger) {
        val playbackContext = PlaybackContext(
            id = contextId,
            playlist = playlist,
            trigger = trigger,
            repeat = false,
            shuffle = false
        )
        viewModelScope.launch {
            val result = repository.saveContext(playbackContext)
            if (result.isSuccess()) {
                navEvent.postValue(Event(NavEvent.Finish))
            } else {
                result.message?.let {
                    errorEvent.postValue(Event(it))
                }
            }
        }
    }
}

sealed class ContextDetailAction {
    data class Init(val contextToEdit: PlaybackContext?) : ContextDetailAction()
    object ChooseMusic : ContextDetailAction()
    object ChooseTrigger : ContextDetailAction()
    data class TriggerUpdated(val newTrigger: Trigger) : ContextDetailAction()
    data class PlaybackUpdated(val playlist: Playlist) : ContextDetailAction()
    object Save : ContextDetailAction()
}

data class UiState(
    val errorEvent: LiveData<Event<String>>,
    val playbackUiState: LiveData<PlaybackUiState>,
    val triggerUiState: LiveData<TriggerUiState>,
    val navEvent: LiveData<Event<NavEvent>>
)

sealed class TriggerUiState {
    object Unset : TriggerUiState()
    data class CanEdit(val triggerName: String) : TriggerUiState()
}

sealed class PlaybackUiState {
    object Unset : PlaybackUiState()
    data class CanEdit(val musicTitle: String?, val albumArtUrl: String?) : PlaybackUiState()
}

sealed class NavEvent {
    object SetMusic : NavEvent()
    object SetTrigger : NavEvent()
    object Finish : NavEvent()
}