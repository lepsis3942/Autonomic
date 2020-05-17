package com.cjapps.persistence.entity

import androidx.room.*

@Entity(
    tableName = "playlist",
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
internal data class Playlist(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(name = "snapshot_id") val snapshotId: String,
    @Embedded(prefix = "user_") val user: SpotifyUser,
    @ColumnInfo(name = "context_id") var contextId: Long = 0,
    val urn: String
)

internal data class PlaylistWithImages(
    @Embedded val playlist: Playlist,
    @Relation(
        parentColumn = "id",
        entityColumn = "playlist_id"
    )
    val images: List<Image>
)