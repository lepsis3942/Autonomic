package com.cjapps.utility.coroutines.testing

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

@ExperimentalCoroutinesApi
class TestCoroutineRule(
    val coroutineDispatcherProvider: TestCoroutineDispatcherProvider = TestCoroutineDispatcherProvider(),
    val coroutineScope: TestCoroutineScope = TestCoroutineScope(coroutineDispatcherProvider.testCoroutineDispatcher)
) : TestRule {

    override fun apply(base: Statement?, description: Description?) = object : Statement() {
        @Throws(Throwable::class)
        override fun evaluate() {
            Dispatchers.setMain(coroutineDispatcherProvider.testCoroutineDispatcher)

            base?.evaluate()

            Dispatchers.resetMain()
            coroutineScope.cleanupTestCoroutines()
        }
    }

    fun runBlockingTest(testCodeBlock: suspend TestCoroutineScope.() -> Unit) =
        coroutineScope.runBlockingTest { testCodeBlock() }
}