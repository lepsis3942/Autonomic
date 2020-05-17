package com.cjapps.persistence.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "context")
internal data class Context(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val repeat: Boolean,
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