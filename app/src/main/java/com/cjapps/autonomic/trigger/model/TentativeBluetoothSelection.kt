package com.cjapps.autonomic.trigger.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TentativeBluetoothSelection(val name: String, val macAddress: String, val deviceType: TriggerDeviceType): Parcelable