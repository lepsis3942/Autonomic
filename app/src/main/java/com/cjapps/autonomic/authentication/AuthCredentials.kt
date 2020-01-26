package com.cjapps.autonomic.authentication

/**
 * Created by cjgonz on 2020-01-19.
 */
data class AuthCredentials(val accessToken: String, val refreshToken: String, val expiryTimestamp: Long)