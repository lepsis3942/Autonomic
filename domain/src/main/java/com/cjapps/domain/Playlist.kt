package com.cjapps.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Playlist(
    var id: Long = 0,
    var images: List<Image>,
    var snapshotId: String,
    var title: String,
    var user: SpotifyUser,
    var urn: String
): Parcelable