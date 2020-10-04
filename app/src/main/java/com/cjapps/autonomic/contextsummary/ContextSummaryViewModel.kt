package com.cjapps.autonomic.contextsummary

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.domain.PlaybackContext
import com.cjapps.network.authentication.IAuthenticationManager
import com.cjapps.utility.livedata.Event
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContextSummaryViewModel @Inject constructor(
    private val authenticationManager: IAuthenticationManager,
    private val repository: PlaybackContextSummaryRepository
): ViewModel() {
    private val contextItemsUiStateLiveData = MutableLiveData<ContextItemsUiState>()

    val navigationEventLiveData by lazy { MutableLiveData<Event<NavDestination>>() }
    val uiState = UiState(
        contextItemsUiStateLiveData
    )

    fun executeAction(action: ContextSummaryAction) {
        when (action) {
            ContextSummaryAction.Initialize -> initialize()
            is ContextSummaryAction.ContextSelectedToEdit -> navigationEventLiveData.value = Event(NavDestination.ContextEdit(action.context))
            is ContextSummaryAction.CreateButtonTapped -> navigationEventLiveData.value = Event(NavDestination.ContextCreation)
        }
    }

    private fun initialize() {
        if (!authenticationManager.hasRegistered()) {
            navigationEventLiveData.value = Event(NavDestination.Login)
            return
        }

        viewModelScope.launch {
            contextItemsUiStateLiveData.value = ContextItemsUiState.Loading
            val contexts = repository.getContexts().toList()
            contextItemsUiStateLiveData.value = if (contexts.isNullOrEmpty()) {
                ContextItemsUiState.Empty
            } else {
                ContextItemsUiState.ItemsRetrieved(contexts)
            }
        }
    }
}

data class UiState(
    val contextItemsStateLiveData: LiveData<ContextItemsUiState>
)

sealed class ContextItemsUiState {
    object Empty: ContextItemsUiState()
    object Loading: ContextItemsUiState()
    data class ItemsRetrieved(val items: List<PlaybackContext>): ContextItemsUiState()
}

sealed class ContextSummaryAction {
    data class ContextSelectedToEdit(val context: PlaybackContext) : ContextSummaryAction()
    object CreateButtonTapped : ContextSummaryAction()
    object Initialize: ContextSummaryAction()
}

sealed class NavDestination {
    object Login: NavDestination()
    object ContextCreation: NavDestination()
    data class ContextEdit(val contextToEdit: PlaybackContext): NavDestination()
}