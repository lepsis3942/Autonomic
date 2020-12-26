package com.cjapps.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaybackContext(
    var id: Long = 0,
    var playlist: Playlist,
    var repeat: Repeat,
    var shuffle: Boolean,
    var trigger: Trigger
): Parcelable