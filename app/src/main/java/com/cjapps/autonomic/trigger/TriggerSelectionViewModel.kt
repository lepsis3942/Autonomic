package com.cjapps.autonomic.trigger

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cjapps.domain.Trigger
import kotlinx.coroutines.launch
import javax.inject.Inject

class TriggerSelectionViewModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepository,
    private val triggerRepository: TriggerRepository
) : ViewModel() {
    private val bluetoothInfoLiveData = MutableLiveData<BluetoothInfo>()

    val uiState = TriggerSelectionUiState(
        bluetoothInfoLiveData
    )

    fun executeAction(action: TriggerSelectionAction) {
        when (action) {
            TriggerSelectionAction.Init -> {
                retrieveBluetoothList()
            }
            is TriggerSelectionAction.BroadcastReceivedAction -> handleBroadcastReceived(action.intent)
            is TriggerSelectionAction.TriggerSelected -> bluetoothInfoLiveData.value =
                BluetoothInfo.TriggerSelected(action.selectedTrigger)
        }
    }

    private fun handleBroadcastReceived(intent: Intent?) {
        val action = intent?.action ?: return
        if (action == BluetoothAdapter.ACTION_STATE_CHANGED) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)
            if (state == BluetoothAdapter.STATE_ON) {

            }
        }
    }

    private fun retrieveBluetoothList() {
        viewModelScope.launch {
            val unavailableTriggers = triggerRepository.getUnavailableTriggerMacAddresses()
            val knownDevices = bluetoothRepository.getBluetoothDevices()
                .filterNot { device -> unavailableTriggers.contains(device.macAddress) }
            bluetoothInfoLiveData.value = BluetoothInfo.KnownDevices(knownDevices)
        }
    }
}

sealed class TriggerSelectionAction {
    object Init : TriggerSelectionAction()
    data class BroadcastReceivedAction(val intent: Intent?) : TriggerSelectionAction()
    data class TriggerSelected(val selectedTrigger: Trigger) : TriggerSelectionAction()
}

sealed class BluetoothInfo {
    object StateDisabled : BluetoothInfo()
    data class KnownDevices(val deviceList: List<Trigger>) : BluetoothInfo()
    data class TriggerSelected(val selectedTrigger: Trigger) : BluetoothInfo()
}

data class TriggerSelectionUiState(val bluetoothInfo: LiveData<BluetoothInfo>)