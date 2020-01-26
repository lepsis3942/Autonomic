package com.cjapps.autonomic.login

/**
 * Created by cjgonz on 2019-09-18.
 */
interface ILoginRepository {
    fun getAuthorizationScopes(): Array<String>
    fun getClientId(): String
    fun getRedirectUri(): String
    suspend fun getTokenFromCode(code: String, redirectUrl: String): Boolean
}