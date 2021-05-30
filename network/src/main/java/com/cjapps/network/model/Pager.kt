package com.cjapps.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Pager<T : Parcelable>(
    var href: String? = null,
    var items: List<T>? = null,
    var limit: Int = 0,
    var next: String? = null,
    var offset: Int = 0,
    var previous: String? = null,
    var total: Int = 0
) : Parcelable