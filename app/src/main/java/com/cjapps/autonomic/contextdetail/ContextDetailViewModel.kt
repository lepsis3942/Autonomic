package com.cjapps.autonomic.contextdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjapps.domain.Trigger
import com.cjapps.network.model.PlaylistSimple
import com.cjapps.utility.livedata.Event
import javax.inject.Inject

class ContextDetailViewModel @Inject constructor(

) : ViewModel() {
    private val playbackUiState = MutableLiveData<PlaybackUiState>()
    private val triggerUiState = MutableLiveData<TriggerUiState>()
    private val navEvent = MutableLiveData<Event<NavEvent>>()
    private var isUiDirty = false
    private var isInitialized = false
    private var currentlySelectedTrigger: Trigger? = null
    private var currentlySelectedPlayback: PlaylistSimple? = null

    val viewState = UiState(
        playbackUiState,
        triggerUiState,
        navEvent
    )

    fun executeAction(action: ContextDetailAction) {
        when (action) {
            is ContextDetailAction.Init -> initialize(action.contextIdToEdit)
            is ContextDetailAction.ChooseMusic -> navEvent.value = Event(NavEvent.SetMusic)
            is ContextDetailAction.ChooseTrigger -> navEvent.value = Event(NavEvent.SetTrigger)
            is ContextDetailAction.TriggerUpdated -> handleTriggerUpdate(action.newTrigger)
            is ContextDetailAction.PlaybackUpdated -> handlePlaybackUpdated(action.playlist)
            is ContextDetailAction.Save -> handleSave()
        }
    }

    private fun initialize(contextIdToEdit: Int) {
        if (isInitialized) return

        if (contextIdToEdit >= 0) {
            // Edit mode
        } else {
            triggerUiState.value = TriggerUiState.Unset
            playbackUiState.value = PlaybackUiState.Unset
        }
        isInitialized = true
    }

    private fun handleTriggerUpdate(newTrigger: Trigger) {
        val currentTrigger = currentlySelectedTrigger
        if (currentTrigger != null && currentTrigger.macAddress == newTrigger.macAddress) return

        isUiDirty = true
        currentlySelectedTrigger = newTrigger
        currentlySelectedTrigger?.let { triggerUiState.value = TriggerUiState.CanEdit(it.name) }
    }

    private fun handlePlaybackUpdated(playlist: PlaylistSimple) {
        val currentPlayback = currentlySelectedPlayback
        if (currentPlayback != null && currentPlayback.id == playlist.id) return

        isUiDirty = true
        currentlySelectedPlayback = playlist
        currentlySelectedPlayback?.let { playbackUiState.value = PlaybackUiState.CanEdit(it.name, it.images?.firstOrNull()?.url) }
    }

    private fun handleSave() {
        TODO("Ensure trigger not duplicated in DB, persist settings")
    }
}

sealed class ContextDetailAction {
    data class Init(val contextIdToEdit: Int) : ContextDetailAction()
    object ChooseMusic : ContextDetailAction()
    object ChooseTrigger : ContextDetailAction()
    data class TriggerUpdated(val newTrigger: Trigger) : ContextDetailAction()
    data class PlaybackUpdated(val playlist: PlaylistSimple) : ContextDetailAction()
    object Save : ContextDetailAction()
}

data class UiState(
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