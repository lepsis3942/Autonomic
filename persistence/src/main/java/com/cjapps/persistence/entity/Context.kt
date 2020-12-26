package com.cjapps.persistence.entity

import androidx.room.*

@TypeConverters(RepeatTypeTypeConverter::class)
@Entity(tableName = "context")
internal data class Context(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repeat: RepeatType,
    val shuffle: Boolean
)

internal data class FullContext(
    @Embedded val context: Context,
    @Relation(
        parentColumn = "id",
        entityColumn = "context_id"
    )
    val trigger: Trigger,
    @Relation(
        parentColumn = "id",
        entityColumn = "context_id",
        entity = Playlist::class
    )
    val playlistAndImages: PlaylistWithImages
)

internal enum class RepeatType(val rawValue: Int) {
    NONE(0),
    ALL(1),
    ONCE(2)
}

internal class RepeatTypeTypeConverter {
    @TypeConverter
    fun toRawValue(repeatType: RepeatType): Int {
        return repeatType.rawValue
    }

    @TypeConverter
    fun fromRawValue(value: Int): RepeatType {
        return when (value) {
            0 -> RepeatType.NONE
            1 -> RepeatType.ALL
            2 -> RepeatType.ONCE
            else -> RepeatType.NONE
        }
    }
}