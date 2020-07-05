package com.cjapps.network.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Image(
    var width: Int? = null,
    var height: Int? = null,
    var url: String? = null
) : Parcelable