package com.cjapps.network.model

import kotlinx.android.parcel.Parcelize

/**
 * Created by cjgonz on 2020-01-26.
 */
@Parcelize
data class UserPrivate(
    var birthdate: String? = null,
    var country: String? = null,
    var email: String? = null,
    var product: String? = null
) : UserPublic()