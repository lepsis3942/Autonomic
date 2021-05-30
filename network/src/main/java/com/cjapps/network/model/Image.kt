package com.cjapps.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    var width: Int? = null,
    var height: Int? = null,
    var url: String? = null
) : Parcelable