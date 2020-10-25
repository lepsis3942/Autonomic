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
    private val createContextUiStateLiveData = MutableLiveData<CreateContextButtonUiState>()

    val navigationEventLiveData by lazy { MutableLiveData<Event<NavDestination>>() }
    val uiState = UiState(
        contextItemsUiStateLiveData,
        createContextUiStateLiveData
    )

    fun executeAction(action: ContextSummaryAction) {
        when (action) {
            ContextSummaryAction.Initialize -> initialize()
            is ContextSummaryAction.ContextSelectedToEdit -> navigationEventLiveData.value = Event(NavDestination.ContextEdit(action.context))
            is ContextSummaryAction.CreateButtonTapped -> navigationEventLiveData.value = Event(NavDestination.ContextCreation)
            is ContextSummaryAction.ContextListScrolled -> {
                val scrollThreshold = 10
                if (action.dy > scrollThreshold && action.createButtonShowing) {
                    // Scroll down
                    createContextUiStateLiveData.value = CreateContextButtonUiState.Hide
                } else if (action.dy < -scrollThreshold) {
                    // Scroll up
                    createContextUiStateLiveData.value = CreateContextButtonUiState.Show
                }
            }
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
    val contextItemsStateLiveData: LiveData<ContextItemsUiState>,
    val createContextButtonUiState: LiveData<CreateContextButtonUiState>
)

sealed class ContextItemsUiState {
    object Empty: ContextItemsUiState()
    object Loading: ContextItemsUiState()
    data class ItemsRetrieved(val items: List<PlaybackContext>): ContextItemsUiState()
}

sealed class CreateContextButtonUiState {
    object Show: CreateContextButtonUiState()
    object Hide: CreateContextButtonUiState()
}

sealed class ContextSummaryAction {
    object CreateButtonTapped : ContextSummaryAction()
    data class ContextListScrolled(val dy: Int, val createButtonShowing: Boolean) : ContextSummaryAction()
    data class ContextSelectedToEdit(val context: PlaybackContext) : ContextSummaryAction()
    object Initialize: ContextSummaryAction()
}

sealed class NavDestination {
    object Login: NavDestination()
    object ContextCreation: NavDestination()
    data class ContextEdit(val contextToEdit: PlaybackContext): NavDestination()
}