package com.cjapps.domain

data class Image(
    var id: Long = 0,
    var size: ImageSize,
    var url: String
)

enum class ImageSize {
    SMALL,
    MEDIUM,
    LARGE
}