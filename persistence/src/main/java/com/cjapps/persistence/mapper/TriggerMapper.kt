package com.cjapps.persistence.mapper

import com.cjapps.domain.Trigger
import com.cjapps.domain.TriggerDeviceType
import com.cjapps.persistence.entity.Trigger as EntityTrigger
import com.cjapps.persistence.entity.TriggerDeviceType as EntityTriggerDeviceType

internal fun EntityTrigger.toDomain(): Trigger {
    return Trigger(
        id = id,
        deviceType = deviceType.toDomain(),
        macAddress = macAddress,
        name = name
    )
}

internal fun EntityTriggerDeviceType.toDomain(): TriggerDeviceType {
    return when(this) {
        EntityTriggerDeviceType.BLUETOOTH_GENERIC -> TriggerDeviceType.BLUETOOTH_GENERIC
        EntityTriggerDeviceType.BLUETOOTH_AUDIO -> TriggerDeviceType.BLUETOOTH_AUDIO
        EntityTriggerDeviceType.BLUETOOTH_COMPUTER -> TriggerDeviceType.BLUETOOTH_COMPUTER
        EntityTriggerDeviceType.BLUETOOTH_VEHICLE_AUDIO -> TriggerDeviceType.BLUETOOTH_VEHICLE_AUDIO
        EntityTriggerDeviceType.BLUETOOTH_WEARABLE -> TriggerDeviceType.BLUETOOTH_WEARABLE
    }
}

internal fun Trigger.toEntity(contextId: Long): EntityTrigger {
    return EntityTrigger(
        id = id,
        deviceType = deviceType.toEntity(),
        macAddress = macAddress,
        name = name,
        contextId = contextId
    )
}

internal fun TriggerDeviceType.toEntity(): EntityTriggerDeviceType {
    return when(this) {
        TriggerDeviceType.BLUETOOTH_GENERIC -> EntityTriggerDeviceType.BLUETOOTH_GENERIC
        TriggerDeviceType.BLUETOOTH_AUDIO -> EntityTriggerDeviceType.BLUETOOTH_AUDIO
        TriggerDeviceType.BLUETOOTH_COMPUTER -> EntityTriggerDeviceType.BLUETOOTH_COMPUTER
        TriggerDeviceType.BLUETOOTH_VEHICLE_AUDIO -> EntityTriggerDeviceType.BLUETOOTH_VEHICLE_AUDIO
        TriggerDeviceType.BLUETOOTH_WEARABLE -> EntityTriggerDeviceType.BLUETOOTH_WEARABLE
    }
}