package com.cjapps.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlaylistTracksInformation(var href: String? = null, var total: Int = 0) : Parcelable