package com.cjapps.network.di

import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

/**
 * Created by cjgonz on 2020-02-09.
 */
class TimberHttpLoggingInterceptor: HttpLoggingInterceptor.Logger {
    override fun log(message: String) {
        Timber.d(message)
    }
}