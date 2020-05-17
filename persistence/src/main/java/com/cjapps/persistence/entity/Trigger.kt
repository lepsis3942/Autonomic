package com.cjapps.persistence.entity

import androidx.room.*

@Entity(
    tableName = "trigger",
    foreignKeys = [
        ForeignKey(
            entity = Context::class,
            parentColumns = ["id"],
            childColumns = ["context_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("context_id")]
)
@TypeConverters(TriggerDeviceTypeConverter::class)
internal data class Trigger(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "mac_address") val macAddress: String,
    @ColumnInfo(name = "context_id") var contextId: Long = 0,
    val name: String,
    @ColumnInfo(name = "device_type") val deviceType: TriggerDeviceType
)

internal enum class TriggerDeviceType(val rawValue: Int) {
    BLUETOOTH_GENERIC(0),
    BLUETOOTH_AUDIO(1),
    BLUETOOTH_COMPUTER(2),
    BLUETOOTH_VEHICLE_AUDIO(3),
    BLUETOOTH_WEARABLE(4)
}

internal class TriggerDeviceTypeConverter {
    @TypeConverter
    fun toRawValue(deviceType: TriggerDeviceType): Int {
        return deviceType.rawValue
    }

    @TypeConverter
    fun fromRawValue(value: Int): TriggerDeviceType {
        return when(value) {
            0 -> TriggerDeviceType.BLUETOOTH_GENERIC
            1 -> TriggerDeviceType.BLUETOOTH_AUDIO
            2 -> TriggerDeviceType.BLUETOOTH_COMPUTER
            3 -> TriggerDeviceType.BLUETOOTH_VEHICLE_AUDIO
            4 -> TriggerDeviceType.BLUETOOTH_WEARABLE
            else -> TriggerDeviceType.BLUETOOTH_GENERIC
        }
    }
}