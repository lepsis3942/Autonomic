package com.cjapps.autonomic.view

import com.cjapps.autonomic.R
import com.cjapps.domain.TriggerDeviceType

fun TriggerDeviceType.toResourceId(): Int {
    return when (this) {
        TriggerDeviceType.BLUETOOTH_VEHICLE_AUDIO -> R.drawable.ic_car_black
        TriggerDeviceType.BLUETOOTH_AUDIO -> R.drawable.ic_headset_black
        TriggerDeviceType.BLUETOOTH_COMPUTER -> R.drawable.ic_computer_black
        TriggerDeviceType.BLUETOOTH_WEARABLE -> R.drawable.ic_devices_other_black
        TriggerDeviceType.BLUETOOTH_GENERIC -> R.drawable.ic_bluetooth_black
    }
}