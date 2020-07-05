package com.cjapps.utility.coroutines

import kotlinx.coroutines.CoroutineDispatcher

interface ICoroutineDispatcherProvider {
    val Main: CoroutineDispatcher
    val IO: CoroutineDispatcher
    val Default: CoroutineDispatcher
}