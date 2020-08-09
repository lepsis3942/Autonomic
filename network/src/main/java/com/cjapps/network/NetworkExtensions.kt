package com.cjapps.network

import com.cjapps.utility.Resource
import com.cjapps.utility.Status
import retrofit2.Response

/**
 * Created by cjgonz on 2019-09-28.
 */
fun <K> Response<K>.toResource() : Resource<K> {
    return if (this.isSuccessful) {
        Resource.success(this.body())
    } else {
        val errorBodyMsg = this.errorBody()?.toString()
        val errorMsg = if (errorBodyMsg.isNullOrEmpty()) {
            this.message()
        } else {
            errorBodyMsg
        }
        Resource.error(errorMsg ?: "unknown error occurred", null)
    }
}

fun <K> Resource<K>.isSuccess() : Boolean {
    return this.status == Status.SUCCESS
}

fun <K> Response<K>.isSuccess() : Boolean {
    return this.isSuccessful
}