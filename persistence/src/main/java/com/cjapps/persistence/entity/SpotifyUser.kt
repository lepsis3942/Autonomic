package com.cjapps.persistence.entity

import androidx.room.ColumnInfo

internal data class SpotifyUser(
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "urn") val urn: String
)