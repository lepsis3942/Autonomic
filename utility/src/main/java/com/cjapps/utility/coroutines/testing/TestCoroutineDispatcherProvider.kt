package com.cjapps.utility.coroutines.testing

import com.cjapps.utility.coroutines.ICoroutineDispatcherProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher

@ExperimentalCoroutinesApi
class TestCoroutineDispatcherProvider : ICoroutineDispatcherProvider {
    var testCoroutineDispatcher = TestCoroutineDispatcher()
    override val Main = testCoroutineDispatcher
    override val IO = testCoroutineDispatcher
    override val Default = testCoroutineDispatcher
}