package com.cjapps.network.authentication

import com.cjapps.network.AuthTokenData
import com.cjapps.network.authentication.AuthenticationManager.Companion.EXPIRY_TIME_BUFFER
import com.cjapps.utility.Resource
import com.cjapps.utility.coroutines.testing.TestCoroutineDispatcherProvider
import com.cjapps.utility.coroutines.testing.TestCoroutineRule
import io.mockk.*
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneId

@ExperimentalCoroutinesApi
class AuthenticationManagerTest {
    private val coroutineDispatcher = TestCoroutineDispatcherProvider()
    @get:Rule val coroutineRule = TestCoroutineRule(coroutineDispatcher)

    @RelaxedMockK private lateinit var mockRepository: IAuthenticationRepository
    private lateinit var authManger: AuthenticationManager

    private var systemTime = 0L
    private lateinit var fixedClock: Clock

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        mockkStatic(Clock::class)
        systemTime = 0L
        resetSystemTime()

        authManger = AuthenticationManager(mockRepository, coroutineDispatcher)
    }

    @Test
    fun authInfoPersistedToDataProvider() {
        resetSystemTime(useTime = 45960309L)
        val data = AuthTokenData("access_token", "refresh_token", 3600)

        authManger.setAuthInfo(data)

        val expectedCredentials = AuthenticationCredentials(
            accessToken = data.accessToken,
            refreshToken = data.refreshToken,
            expiryTimestamp = systemTime + (data.expiresIn * 1000)
        )
        verify { mockRepository.storeCredentials(expectedCredentials) }
    }

    @Test
    fun verifyCredentialsCached() = runBlockingTest {
        resetSystemTime(useTime = 45960309L)
        val data = AuthTokenData("access_token", "refresh_token", 3600)

        authManger.setAuthInfo(data)

        val expectedCredentials = AuthenticationCredentials(
            accessToken = data.accessToken,
            refreshToken = data.refreshToken,
            expiryTimestamp = systemTime + (data.expiresIn * 1000)
        )

        assertEquals(expectedCredentials, authManger.getAuthInfo())
        verify(exactly = 0) { mockRepository.getStoredCredentials() }
    }

    @Test
    fun verifyExistingValidCredentialsReturned() = coroutineRule.runBlockingTest {
        resetSystemTime(useTime = 45960309L)
        val credentials = AuthenticationCredentials("access_token", "refresh_token", systemTime + EXPIRY_TIME_BUFFER + 1)
        every { mockRepository.getStoredCredentials() } returns credentials

        val resultCredentials = authManger.getAuthInfo()

        assertEquals(credentials, resultCredentials)
        advanceUntilIdle()
        coVerify(exactly = 0) { mockRepository.refreshAccessToken(any()) }
    }

    @Test
    fun verifyExpiredCredentialsRefreshed() = coroutineRule.runBlockingTest {
        val networkAuthTokenData = wireUpAuthManagerWithExpiredCredentials()

        val resultCredentials = authManger.getAuthInfo()

        assertEquals(networkAuthTokenData.refreshToken, resultCredentials.refreshToken)
    }

    @Test
    fun verifyRefreshedCredentialsAreCached() = coroutineRule.runBlockingTest {
        val networkAuthTokenData = wireUpAuthManagerWithExpiredCredentials()

        val resultCredentials = authManger.getAuthInfo()

        advanceUntilIdle()
        assertEquals(networkAuthTokenData.refreshToken, resultCredentials.refreshToken)

        authManger.getAuthInfo()

        assertEquals(networkAuthTokenData.refreshToken, resultCredentials.refreshToken)
        coVerify(exactly = 1) { mockRepository.refreshAccessToken(any()) }
        coVerify(exactly = 1) { mockRepository.storeCredentials(resultCredentials) }
    }

    @Test
    fun verifyRetryAttemptedOnRefreshNetworkFail() = coroutineRule.runBlockingTest {
        val networkAuthTokenData = wireUpAuthManagerWithExpiredCredentials()
        coEvery {
            mockRepository.refreshAccessToken(any())
        } throws Exception() andThenThrows Exception() andThen Resource.success(networkAuthTokenData)


        val resultCredentials = authManger.getAuthInfo()

        advanceUntilIdle()
        assertEquals(networkAuthTokenData.refreshToken, resultCredentials.refreshToken)

        authManger.getAuthInfo()

        coVerify(exactly = 3) { mockRepository.refreshAccessToken(any()) }
        assertEquals(networkAuthTokenData.refreshToken, resultCredentials.refreshToken)
    }

    @Throws(Exception::class)
    @Test
    fun verifyRetryAttemptsExceededFromNetworkFailureThrowsException() = coroutineRule.runBlockingTest {
        wireUpAuthManagerWithExpiredCredentials()
        coEvery {
            mockRepository.refreshAccessToken(any())
        } returns Resource.error("", null) andThen Resource.error("", null) andThen Resource.error("", null)
    }

    @Throws(Exception::class)
    @Test
    fun verifyRetryAttemptsExceededFromExceptionThrowsException() = coroutineRule.runBlockingTest {
        wireUpAuthManagerWithExpiredCredentials()
        coEvery {
            mockRepository.refreshAccessToken(any())
        } throws Exception() andThenThrows Exception() andThenThrows Exception()
    }

    private fun wireUpAuthManagerWithExpiredCredentials(): AuthTokenData {
        resetSystemTime(useTime = 45960309L)
        val credentials = AuthenticationCredentials("access_token", "refresh_token", systemTime + EXPIRY_TIME_BUFFER - 1)
        val networkAuthTokenData = AuthTokenData("access_token", "NEW_refresh_token", expiresIn = 3600)
        every { mockRepository.getStoredCredentials() } returns credentials
        coEvery { mockRepository.refreshAccessToken(credentials.refreshToken) } returns Resource.success(networkAuthTokenData)
        return networkAuthTokenData
    }

    private fun resetSystemTime(useTime: Long? = null) {
        if (useTime != null) systemTime = useTime
        fixedClock = Clock.fixed(Instant.ofEpochMilli(systemTime), ZoneId.systemDefault())
        every { Clock.systemUTC() } returns fixedClock
    }
}
