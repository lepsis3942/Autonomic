package com.cjapps.persistence.entity

import androidx.room.*

@TypeConverters(ImageSizeTypeConverter::class)
@Entity(
    tableName = "image",
    foreignKeys = [
        ForeignKey(
            entity = Playlist::class,
            parentColumns = ["id"],
            childColumns = ["playlist_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("playlist_id")]
)
internal data class Image(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "playlist_id") var playlistId: Long = 0,
    val url: String,
    val size: ImageSize
)

internal enum class ImageSize(val rawValue: Int) {
    SMALL(0),
    MEDIUM(1),
    LARGE(2)
}

internal class ImageSizeTypeConverter {
    @TypeConverter
    fun toRawValue(imageSize: ImageSize): Int {
        return imageSize.rawValue
    }

    @TypeConverter
    fun fromRawValue(value: Int): ImageSize {
        return when(value) {
            0 -> ImageSize.SMALL
            1 -> ImageSize.MEDIUM
            2 -> ImageSize.LARGE
            else -> ImageSize.MEDIUM
        }
    }
}

