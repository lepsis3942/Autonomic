package com.cjapps.persistence.mapper

import com.cjapps.domain.*
import com.cjapps.persistence.entity.PlaylistWithImages
import com.cjapps.persistence.entity.Image as EntityImage
import com.cjapps.persistence.entity.ImageSize as EntityImageSize
import com.cjapps.persistence.entity.Playlist as EntityPlaylist
import com.cjapps.persistence.entity.SpotifyUser as EntitySpotifyUser

internal fun PlaylistWithImages.toDomain(): Playlist {
    return Playlist(
        id = playlist.id,
        images = images.map { it.toDomain() },
        snapshotId = playlist.snapshotId,
        title = playlist.title,
        user = playlist.user.toDomain(),
        urn = playlist.urn
    )
}

internal fun EntitySpotifyUser.toDomain(): SpotifyUser {
    return SpotifyUser(name = name, urn = urn)
}

internal fun EntityImage.toDomain(): Image {
    return Image(
        id = id,
        size = size.toDomain(),
        url = url
    )
}

internal fun EntityImageSize.toDomain(): ImageSize {
    return when(this) {
        EntityImageSize.SMALL -> ImageSize.SMALL
        EntityImageSize.MEDIUM -> ImageSize.MEDIUM
        EntityImageSize.LARGE -> ImageSize.LARGE
    }
}

internal fun Playlist.toEntity(context: PlaybackContext): PlaylistWithImages {
    return toEntity(context.id)
}

internal fun Playlist.toEntity(contextId: Long): PlaylistWithImages {
    val playlist = EntityPlaylist(
        contextId = contextId,
        id = id,
        snapshotId = snapshotId,
        title = title,
        user = user.toEntity(),
        urn = urn
    )
    return PlaylistWithImages(
        playlist = playlist,
        images = images.map { it.toEntity(playlistId = playlist.id) }
    )
}

internal fun SpotifyUser.toEntity(): EntitySpotifyUser {
    return EntitySpotifyUser(name = name, urn = urn)
}

internal fun Image.toEntity(playlistId: Long): EntityImage {
    return EntityImage(
        id = id,
        size = size.toEntity(),
        url = url,
        playlistId = playlistId
    )
}

internal fun ImageSize.toEntity(): EntityImageSize {
    return when(this) {
        ImageSize.SMALL -> EntityImageSize.SMALL
        ImageSize.MEDIUM -> EntityImageSize.MEDIUM
        ImageSize.LARGE -> EntityImageSize.LARGE
    }
}