package com.cjapps.persistence

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.cjapps.domain.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

/**
 * Created by cjgonz on 5/10/20.
 */
class AutonomicDatabaseInteractorTest {
    private lateinit var interactor: AutonomicDatabaseInteractor

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        interactor = AutonomicDatabaseInteractor(context, runDbInMemory = true)
    }

    @After
    fun tearDown() {
        interactor.clearDb()
    }

    @Test
    fun testInsertContext() = runBlocking {
        val context = getDomainContext()

        interactor.insertContext(context)

        val contextList = interactor.getAllContexts()
        assertEquals(1, contextList.size)
        assertEquals(1, interactor.getNumberPlaylists())
        val retrievedContext = contextList.first()
        assertEquals(context.playlist.snapshotId, retrievedContext.playlist.snapshotId)
        assertEquals(context.playlist.user.name, retrievedContext.playlist.user.name)
        assertEquals(3, retrievedContext.playlist.images.size)
        assertEquals(context.trigger.macAddress, retrievedContext.trigger.macAddress)
    }

    @Test
    fun testInsertMultipleContexts() = runBlocking {
        val context = getDomainContext()

        for (i in 0..5) {
            interactor.insertContext(context)
        }

        val contexts = interactor.getAllContexts()

        assertEquals(6, contexts.size)
        assertEquals(6, interactor.getNumberPlaylists())
    }

    @Test
    fun testDeleteContext() = runBlocking {
        val context = getDomainContext()

        interactor.insertContext(context)

        val contextList = interactor.getAllContexts()
        assertEquals(1, contextList.size)

        interactor.deleteContext(contextList.first())
        assertTrue(interactor.getAllContexts().isEmpty())
        assertEquals(0, interactor.getNumberPlaylists())
    }

    @Test
    fun testUpdateContext() = runBlocking {
        val context = getDomainContext()

        interactor.insertContext(context)

        val existingContexts = interactor.getAllContexts()
        assertEquals(1, existingContexts.size)

        val updatedContext = existingContexts.first().apply {
            repeat = false
            playlist = playlist.apply { snapshotId = "newSnap" }
        }
        interactor.updateContext(updatedContext)

        val updatedContextList = interactor.getAllContexts()
        assertEquals(1, updatedContextList.size)
        assertEquals(1, interactor.getNumberPlaylists())
        assertEquals(existingContexts.first().id, updatedContextList.first().id)
        assertEquals("newSnap", updatedContextList.first().playlist.snapshotId)
        assertEquals(false, updatedContextList.first().repeat)
    }

    private fun getDomainContext(): PlaybackContext {
        val images = listOf(
            Image(url = "image 1 url", size = ImageSize.SMALL),
            Image(url = "image 2 url", size = ImageSize.MEDIUM),
            Image(url = "image 3 url", size = ImageSize.LARGE)
        )

        val playlist = Playlist(
            snapshotId = "snap",
            user = SpotifyUser("John Doe", "user urn"),
            urn = "playlist urn",
            images = images
        )

        val trigger = Trigger(
            macAddress = "1234",
            name = "Car",
            deviceType = TriggerDeviceType.BLUETOOTH_AUDIO
        )

        return PlaybackContext(
            playlist = playlist,
            trigger = trigger,
            repeat = true,
            shuffle = true
        )
    }
}