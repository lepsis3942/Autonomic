package com.cjapps.persistence

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cjapps.persistence.dao.ContextDao
import com.cjapps.persistence.dao.ImageDao
import com.cjapps.persistence.dao.PlaylistDao
import com.cjapps.persistence.dao.TriggerDao
import com.cjapps.persistence.entity.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import android.content.Context as AndroidContext

class AutonomicDatabaseTest {
    private lateinit var contextDao: ContextDao
    private lateinit var imageDao: ImageDao
    private lateinit var playlistDao: PlaylistDao
    private lateinit var triggerDao: TriggerDao
    private lateinit var db: AutonomicDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<AndroidContext>()
        db = Room.inMemoryDatabaseBuilder(
            context, AutonomicDatabase::class.java).build()
        contextDao = db.contextDao()
        imageDao = db.imageDao()
        playlistDao = db.playlistDao()
        triggerDao = db.triggerDao()
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        db.close()
    }

    @Test
    fun testInsertContext() = runBlocking {
        contextDao.insertFullContext(getTestContext())
        val contexts = contextDao.getAllContexts()

        assertEquals(1, contexts.size)
        val playlistAndImages = contexts.first().playlistAndImages
        assertEquals("playlist urn", playlistAndImages.playlist.urn)
        assertEquals(3, playlistAndImages.images.size)
        assertEquals("image 2 url", playlistAndImages.images[1].url)
    }

    @Test
    fun testVerifyContextDeleteCascade() = runBlocking {
        contextDao.insertFullContext(getTestContext())
        val contexts = contextDao.getAllContexts()
        contextDao.deleteContext(contexts[0].context)

        assertTrue(playlistDao.getAllPlaylists().isEmpty())
        assertTrue(triggerDao.getAllTriggers().isEmpty())
        assertTrue(imageDao.getAllImages().isEmpty())
    }

    @Test
    fun testUpdatePlaylist() = runBlocking {
        contextDao.insertFullContext(getTestContext())
        val oldPlaylists = playlistDao.getAllPlaylists()
        val playlist = oldPlaylists[0].copy(
            urn = "brand new urn",
            snapshotId = "78"
        )
        playlistDao.updatePlaylist(playlist)

        val newPlaylists = playlistDao.getAllPlaylists()
        assertEquals(1, newPlaylists.size)
        assertEquals(playlist, newPlaylists[0])
    }

    private fun getTestContext(): FullContext {
        val playlist = Playlist(
            snapshotId = "",
            user = SpotifyUser("user urn", " John Doe"),
            urn = "playlist urn"
        )

        val images = listOf(
            Image(url = "image 1 url", size = ImageSize.MEDIUM),
            Image(url = "image 2 url", size = ImageSize.MEDIUM),
            Image(url = "image 3 url", size = ImageSize.MEDIUM)
        )

        val context = Context(repeat = true, shuffle = true)
        val trigger = Trigger(
            macAddress = "1234",
            name = "Car",
            deviceType = TriggerDeviceType.BLUETOOTH_VEHICLE_AUDIO
        )

        return FullContext(
            context = context,
            trigger = trigger,
            playlistAndImages = PlaylistWithImages(playlist = playlist, images = images)
        )
    }
}
