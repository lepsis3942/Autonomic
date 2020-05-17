package com.cjapps.autonomic.trigger

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass.Device.AUDIO_VIDEO_CAR_AUDIO
import android.bluetooth.BluetoothClass.Device.Major
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.pm.PackageManager
import com.cjapps.autonomic.trigger.model.TentativeBluetoothSelection
import com.cjapps.autonomic.trigger.model.TriggerDeviceType
import dagger.Reusable
import javax.inject.Inject

@Reusable
class BluetoothRepository @Inject constructor(
    private val appContext: Context
) {

    fun deviceSupportsBluetooth(): Boolean {
        return appContext
            .packageManager
            ?.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH) ?: false
    }

    fun isBluetoothEnabled(): Boolean {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        return adapter.isEnabled
    }

    fun getBluetoothDevices(): List<TentativeBluetoothSelection> {
        val adapter = BluetoothAdapter.getDefaultAdapter()
        return adapter.bondedDevices.map {
            TentativeBluetoothSelection(
                it.name,
                it.address,
                it.toTriggerDeviceType()
            )
        }
    }

    private fun BluetoothDevice.toTriggerDeviceType(): TriggerDeviceType {
        if (this.bluetoothClass.deviceClass == AUDIO_VIDEO_CAR_AUDIO) {
            return TriggerDeviceType.BLUETOOTH_VEHICLE_AUDIO
        }

        return when (this.bluetoothClass.majorDeviceClass) {
            Major.AUDIO_VIDEO -> TriggerDeviceType.BLUETOOTH_AUDIO
            Major.COMPUTER -> TriggerDeviceType.BLUETOOTH_COMPUTER
            Major.WEARABLE -> TriggerDeviceType.BLUETOOTH_WEARABLE
            else -> TriggerDeviceType.BLUETOOTH_GENERIC
        }
    }
}