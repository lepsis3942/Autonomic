package com.cjapps.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Trigger(
    var id: Long = 0,
    var deviceType: TriggerDeviceType,
    var macAddress: String,
    var name: String
): Parcelable

enum class TriggerDeviceType {
    BLUETOOTH_GENERIC,
    BLUETOOTH_AUDIO,
    BLUETOOTH_COMPUTER,
    BLUETOOTH_VEHICLE_AUDIO,
    BLUETOOTH_WEARABLE
}