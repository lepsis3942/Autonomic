package com.cjapps.autonomic.view

import coil.transform.RoundedCornersTransformation

object ViewConstants {
    private const val IMAGE_VIEW_CORNER_RADIUS = 8F

    val IMAGE_VIEW_TRANSFORMATIONS by lazy {
        arrayOf(
            RoundedCornersTransformation(IMAGE_VIEW_CORNER_RADIUS, IMAGE_VIEW_CORNER_RADIUS, IMAGE_VIEW_CORNER_RADIUS, IMAGE_VIEW_CORNER_RADIUS)
        )
    }
}