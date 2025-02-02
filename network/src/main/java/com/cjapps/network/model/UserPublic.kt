package com.cjapps.network.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class UserPublic(
    var display_name: String? = null,
    var external_urls: Map<String?, String?>? = null,
    var href: String? = null,
    var id: String? = null,
    var type: String? = null,
    var uri: String? = null
) : Parcelable