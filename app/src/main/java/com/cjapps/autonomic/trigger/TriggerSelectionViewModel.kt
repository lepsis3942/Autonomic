package com.cjapps.autonomic.trigger

import android.bluetooth.BluetoothAdapter
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cjapps.domain.Trigger
import javax.inject.Inject

class TriggerSelectionViewModel @Inject constructor(
    private val bluetoothRepository: BluetoothRepository
): ViewModel() {
    private val bluetoothInfoLiveData = MutableLiveData<BluetoothInfo>()

    val uiState = TriggerSelectionUiState(
        bluetoothInfoLiveData
    )

    fun executeAction(action: TriggerSelectionAction) {
        when (action) {
            TriggerSelectionAction.Init -> {
                val knownDevices = bluetoothRepository.getBluetoothDevices()
                bluetoothInfoLiveData.value = BluetoothInfo.KnownDevices(knownDevices)
            }
            is TriggerSelectionAction.BroadcastReceivedAction -> handleBroadcastReceived(action.intent)
            is TriggerSelectionAction.TriggerSelected -> bluetoothInfoLiveData.value = BluetoothInfo.TriggerSelected(action.selectedTrigger)
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
}

sealed class TriggerSelectionAction {
    object Init: TriggerSelectionAction()
    data class BroadcastReceivedAction(val intent: Intent?): TriggerSelectionAction()
    data class TriggerSelected(val selectedTrigger: Trigger): TriggerSelectionAction()
}

sealed class BluetoothInfo {
    object StateDisabled: BluetoothInfo()
    data class KnownDevices(val deviceList: List<Trigger>): BluetoothInfo()
    data class TriggerSelected(val selectedTrigger: Trigger): BluetoothInfo()
}

data class TriggerSelectionUiState(val bluetoothInfo: LiveData<BluetoothInfo>)