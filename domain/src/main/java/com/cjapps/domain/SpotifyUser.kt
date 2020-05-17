package com.cjapps.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpotifyUser(var name: String, var urn: String): Parcelable