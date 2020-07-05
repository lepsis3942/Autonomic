package com.cjapps.utility.coroutines

import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class CoroutineDispatcherProvider @Inject constructor() : ICoroutineDispatcherProvider {
    override val Main = Dispatchers.Main
    override val IO = Dispatchers.IO
    override val Default = Dispatchers.Default
}