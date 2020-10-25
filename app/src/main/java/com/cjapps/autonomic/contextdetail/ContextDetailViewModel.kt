package com.cjapps.autonomic.contextdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.autonomic.IResourceProvider
import com.cjapps.autonomic.R
import com.cjapps.domain.PlaybackContext
import com.cjapps.domain.Playlist
import com.cjapps.domain.Trigger
import com.cjapps.network.isSuccess
import com.cjapps.utility.livedata.Event
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContextDetailViewModel @Inject constructor(
    private val repository: ContextDetailRepository,
    private val resourceProvider: IResourceProvider
) : ViewModel() {
    private val errorEvent = MutableLiveData<Event<String>>()
    private val playbackUiState = MutableLiveData<PlaybackUiState>()
    private val toolbarUiState = MutableLiveData<ToolbarUiState>()
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
        toolbarUiState,
        triggerUiState,
        navEvent
    )

    fun executeAction(action: ContextDetailAction) {
        when (action) {
            is ContextDetailAction.Init -> initialize(action.contextToEdit)
            is ContextDetailAction.ChooseMusic -> navEvent.value = Event(NavEvent.SetMusic)
            is ContextDetailAction.ChooseTrigger -> navEvent.value = Event(NavEvent.SetTrigger)
            is ContextDetailAction.DeleteContext -> handleDelete(currentlySelectedPlayback, currentlySelectedTrigger)
            is ContextDetailAction.TriggerUpdated -> handleTriggerUpdate(action.newTrigger)
            is ContextDetailAction.PlaybackUpdated -> handlePlaybackUpdated(action.playlist)
            is ContextDetailAction.Save -> handleSave(currentlySelectedPlayback, currentlySelectedTrigger)
        }
    }

    private fun initialize(contextToEdit: PlaybackContext?) {
        if (isInitialized) return

        val toolbarUi: ToolbarUiState
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
            toolbarUi = ToolbarUiState.ShowDelete
        } else {
            triggerUiState.value = TriggerUiState.Unset
            playbackUiState.value = PlaybackUiState.Unset
            toolbarUi = ToolbarUiState.HideDelete
        }
        isInitialized = true
        toolbarUiState.value = toolbarUi
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

    private fun handleDelete(playlist: Playlist?, trigger: Trigger?) {
        if (playlist == null || trigger == null) {
            return
        }
        viewModelScope.launch {
            repository.deleteContext(createPlaybackContext(playlist, trigger))
            navEvent.postValue(Event(NavEvent.Finish))
        }
    }

    private fun handleSave(playlist: Playlist?, trigger: Trigger?) {
        if (playlist == null || trigger == null) {
            errorEvent.value = Event(resourceProvider.getString(R.string.context_detail_error_select_both))
            return
        }

        val playbackContext = createPlaybackContext(playlist, trigger)
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

    private fun createPlaybackContext(playlist: Playlist, trigger: Trigger): PlaybackContext {
        return PlaybackContext(
            id = contextId,
            playlist = playlist,
            trigger = trigger,
            repeat = false,
            shuffle = false
        )
    }
}

sealed class ContextDetailAction {
    data class Init(val contextToEdit: PlaybackContext?) : ContextDetailAction()
    object ChooseMusic : ContextDetailAction()
    object ChooseTrigger : ContextDetailAction()
    object DeleteContext : ContextDetailAction()
    data class TriggerUpdated(val newTrigger: Trigger) : ContextDetailAction()
    data class PlaybackUpdated(val playlist: Playlist) : ContextDetailAction()
    object Save : ContextDetailAction()
}

data class UiState(
    val errorEvent: LiveData<Event<String>>,
    val playbackUiState: LiveData<PlaybackUiState>,
    val toolbarUiState: LiveData<ToolbarUiState>,
    val triggerUiState: LiveData<TriggerUiState>,
    val navEvent: LiveData<Event<NavEvent>>
)

sealed class ToolbarUiState {
    object HideDelete : ToolbarUiState()
    object ShowDelete : ToolbarUiState()
}

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