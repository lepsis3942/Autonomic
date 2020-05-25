package com.cjapps.network

import com.google.gson.annotations.SerializedName

/**
 * Created by cjgonz on 2019-09-20.
 */
data class AuthTokenData(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String,
    @SerializedName("expires_in") val expiresIn: Int)