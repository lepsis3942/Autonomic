package com.cjapps.autonomic.contextdetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjapps.autonomic.trigger.model.TentativeBluetoothSelection
import com.cjapps.utility.livedata.Event
import javax.inject.Inject

class ContextDetailViewModel @Inject constructor(

): ViewModel() {
    private val triggerUiState = MutableLiveData<TriggerUiState>()
    private val navEvent = MutableLiveData<Event<NavEvent>>()

    val viewState = UiState(
        triggerUiState,
        navEvent
    )

    fun executeAction(action: ContextDetailAction) {
        when (action) {
            is ContextDetailAction.Init -> initialize(action.contextIdToEdit)
            is ContextDetailAction.ChooseTrigger -> navEvent.value = Event(NavEvent.SetTrigger)
//            is ContextDetailAction.TriggerUpdated -> // TODO
        }

    }

    private fun initialize(contextIdToEdit: Int) {
        if (contextIdToEdit >=0) {
            // Edit mode
        } else {
            triggerUiState.value = TriggerUiState.Unset
        }
    }
}

sealed class ContextDetailAction {
    data class Init(val contextIdToEdit: Int): ContextDetailAction()
    object ChooseTrigger: ContextDetailAction()
    data class TriggerUpdated(val newTrigger: TentativeBluetoothSelection): ContextDetailAction()
}

data class UiState(val triggerUiState: LiveData<TriggerUiState>, val navEvent: LiveData<Event<NavEvent>>)

sealed class TriggerUiState {
    object Unset: TriggerUiState()
    data class CanEdit(val triggerName: String)
}

sealed class NavEvent {
    object SetTrigger: NavEvent()
    object SetPlaylist: NavEvent()
    object Finish: NavEvent()
}