package com.cjapps.autonomic.trigger

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.cjapps.autonomic.R
import com.cjapps.autonomic.databinding.FragTriggerSelectBinding
import com.cjapps.autonomic.trigger.model.TentativeBluetoothSelection
import com.cjapps.utility.extensions.saveValueToNavBackStack
import com.cjapps.utility.viewbinding.viewBindingLifecycle
import dagger.android.support.DaggerFragment
import javax.inject.Inject

class TriggerSelectionFragment: DaggerFragment() {
    companion object {
        const val KEY_SELECTED_TENTATIVE_TRIGGER = "key_selected_tentative_trigger"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val deviceListAdapter = TriggerSelectAdapter { triggerSelected(it) }
    private val viewModel by viewModels<TriggerSelectionViewModel> { viewModelFactory }
    private val viewBinding by viewBindingLifecycle { FragTriggerSelectBinding.bind(requireView()) }
    private val bluetoothReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            viewModel.executeAction(TriggerSelectionAction.BroadcastReceivedAction(intent))
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.frag_trigger_select, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewBinding.triggerSelectDeviceList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = deviceListAdapter
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.uiState.apply {
            bluetoothInfo.observe(viewLifecycleOwner, Observer { displayBluetoothInfo(it) })
        }

        viewModel.executeAction(TriggerSelectionAction.Init)
    }

    override fun onResume() {
        super.onResume()
        activity?.registerReceiver(bluetoothReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    override fun onPause() {
        super.onPause()
        activity?.unregisterReceiver(bluetoothReceiver)
    }

    private fun displayBluetoothInfo(bluetoothInfo: BluetoothInfo) {
        when (bluetoothInfo) {
            is BluetoothInfo.StateDisabled -> moveToDisabledBluetoothState()
            is BluetoothInfo.KnownDevices -> moveToDevicesKnownState(bluetoothInfo.deviceList)
            is BluetoothInfo.TriggerSelected -> {
                saveValueToNavBackStack(KEY_SELECTED_TENTATIVE_TRIGGER, bluetoothInfo.selectedTrigger)
                findNavController().popBackStack()
            }
        }
    }

    private fun triggerSelected(tentativeBluetoothSelection: TentativeBluetoothSelection) {
        viewModel.executeAction(TriggerSelectionAction.TriggerSelected(tentativeBluetoothSelection))
    }

    private fun moveToDevicesKnownState(knownDevices: List<TentativeBluetoothSelection>) {
        viewBinding.triggerSelectCallToActionText.visibility = View.GONE

        viewBinding.triggerSelectDeviceList.visibility = View.VISIBLE
        deviceListAdapter.submitList(knownDevices)
    }

    private fun moveToDisabledBluetoothState() {
        viewBinding.triggerSelectDeviceList.visibility = View.GONE

        viewBinding.triggerSelectCallToActionText.apply {
            visibility = View.VISIBLE
            text = getString(R.string.trigger_select_bluetooth_disabled)
        }
    }
}