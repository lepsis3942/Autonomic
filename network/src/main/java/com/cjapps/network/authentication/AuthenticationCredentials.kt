package com.cjapps.network.authentication

data class AuthenticationCredentials(val accessToken: String, val refreshToken: String, val expiryTimestamp: Long)