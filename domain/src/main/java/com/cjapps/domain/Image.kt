package com.cjapps.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(
    var id: Long = 0,
    var size: ImageSize,
    var url: String
): Parcelable

enum class ImageSize {
    SMALL,
    MEDIUM,
    LARGE
}