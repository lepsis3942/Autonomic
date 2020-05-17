package com.cjapps.persistence

import com.cjapps.domain.PlaybackContext
import com.cjapps.persistence.entity.*
import com.cjapps.persistence.mapper.toDomain
import com.cjapps.persistence.mapper.toEntity
import org.junit.Assert.assertEquals
import org.junit.Test
import com.cjapps.domain.Image as DomainImage
import com.cjapps.domain.ImageSize as DomainImageSize
import com.cjapps.domain.Playlist as DomainPlaylist
import com.cjapps.domain.SpotifyUser as DomainSpotifyUser
import com.cjapps.domain.Trigger as DomainTrigger
import com.cjapps.domain.TriggerDeviceType as DomainTriggerDeviceType

/**
 * Test mapping of entities to domain models and vice versa
 */
class MapperTest {

    @Test
    fun testMappingEntityTriggerToDomain() {
        val entityContext = getEntityContext()
        val domainContext = entityContext.toDomain()

        entityContext.trigger.apply {
            assertEquals(id, domainContext.trigger.id)
            assertEquals(DomainTriggerDeviceType.BLUETOOTH_VEHICLE_AUDIO, domainContext.trigger.deviceType)
            assertEquals(macAddress, domainContext.trigger.macAddress)
            assertEquals(name, domainContext.trigger.name)
            assertEquals(contextId, domainContext.id)
        }
    }

    @Test
    fun testMappingEntityPlaylistToDomain() {
        val entityContext = getEntityContext()
        val domainContext = entityContext.toDomain()

        entityContext.playlistAndImages.images.apply {
            assertEquals(size, domainContext.playlist.images.size)
            forEach {
                domainContext.playlist.images.contains(it.toDomain())
                assertEquals(entityContext.playlistAndImages.playlist.id, it.playlistId)
            }
        }
        entityContext.playlistAndImages.playlist.apply {
            assertEquals(id, domainContext.playlist.id)
            assertEquals(user.name, domainContext.playlist.user.name)
            assertEquals(user.urn, domainContext.playlist.user.urn)
            assertEquals(urn, domainContext.playlist.urn)
            assertEquals(snapshotId, domainContext.playlist.snapshotId)
            assertEquals(contextId, domainContext.id)
        }
    }

    @Test
    fun testMappingEntityContextToDomain() {
        val entityContext = getEntityContext()
        val domainContext = entityContext.toDomain()

        entityContext.context.apply {
            assertEquals(id, domainContext.id)
            assertEquals(repeat, domainContext.repeat)
            assertEquals(shuffle, domainContext.shuffle)
        }
    }

    @Test
    fun testMappingDomainTriggerToEntity() {
        val domainContext = getDomainContext()
        val entityContext = domainContext.toEntity()

        domainContext.trigger.apply {
            assertEquals(id, entityContext.trigger.id)
            assertEquals(TriggerDeviceType.BLUETOOTH_AUDIO, entityContext.trigger.deviceType)
            assertEquals(macAddress, entityContext.trigger.macAddress)
            assertEquals(name, entityContext.trigger.name)
            assertEquals(domainContext.id, entityContext.trigger.contextId)
        }
    }

    @Test
    fun testMappingDomainPlaylistToEntity() {
        val domainContext = getDomainContext()
        val entityContext = domainContext.toEntity()

        domainContext.playlist.images.apply {
            assertEquals(size, entityContext.playlistAndImages.images.size)
            forEach {
                entityContext.playlistAndImages.images.contains(it.toEntity(domainContext.playlist.id))
            }
        }
        domainContext.playlist.apply {
            assertEquals(id, entityContext.playlistAndImages.playlist.id)
            assertEquals(user.name, entityContext.playlistAndImages.playlist.user.name)
            assertEquals(user.urn, entityContext.playlistAndImages.playlist.user.urn)
            assertEquals(urn, entityContext.playlistAndImages.playlist.urn)
            assertEquals(snapshotId, entityContext.playlistAndImages.playlist.snapshotId)
        }
    }

    @Test
    fun testMappingDomainContextToEntity() {
        val domainContext = getDomainContext()
        val entityContext = domainContext.toEntity()

        domainContext.apply {
            assertEquals(id, entityContext.context.id)
            assertEquals(repeat, entityContext.context.repeat)
            assertEquals(shuffle, entityContext.context.shuffle)
        }
    }

    private fun getEntityContext(): FullContext {
        val playlist = Playlist(
            id = 4,
            snapshotId = "",
            user = SpotifyUser("John Doe", "user urn"),
            urn = "playlist urn",
            contextId = 8
        )

        val images = listOf(
            Image(url = "image 1 url", size = ImageSize.SMALL, playlistId = playlist.id),
            Image(url = "image 2 url", size = ImageSize.MEDIUM, playlistId = playlist.id),
            Image(url = "image 3 url", size = ImageSize.LARGE, playlistId = playlist.id)
        )

        val context = Context(id = 8, repeat = true, shuffle = true)
        val trigger = Trigger(
            id = 5,
            macAddress = "1234",
            name = "Car",
            deviceType = TriggerDeviceType.BLUETOOTH_VEHICLE_AUDIO,
            contextId = 8
        )

        return FullContext(
            context = context,
            trigger = trigger,
            playlistAndImages = PlaylistWithImages(
                playlist = playlist,
                images = images
            )
        )
    }

    private fun getDomainContext(): PlaybackContext {
        val images = listOf(
            DomainImage(url = "image 1 url", size = DomainImageSize.SMALL),
            DomainImage(url = "image 2 url", size = DomainImageSize.MEDIUM),
            DomainImage(url = "image 3 url", size = DomainImageSize.LARGE)
        )

        val playlist = DomainPlaylist(
            id = 2,
            snapshotId = "",
            user = DomainSpotifyUser("user urn", " John Doe"),
            urn = "playlist urn",
            images = images
        )

        val trigger = DomainTrigger(
            id = 7,
            macAddress = "1234",
            name = "Car",
            deviceType = DomainTriggerDeviceType.BLUETOOTH_AUDIO
        )

        return PlaybackContext(
            id = 3,
            playlist = playlist,
            trigger = trigger,
            repeat = true,
            shuffle = true
        )
    }
}