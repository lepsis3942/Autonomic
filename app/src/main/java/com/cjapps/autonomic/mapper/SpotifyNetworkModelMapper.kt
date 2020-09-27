package com.cjapps.autonomic.mapper

import com.cjapps.domain.Image
import com.cjapps.domain.ImageSize
import com.cjapps.domain.Playlist
import com.cjapps.domain.SpotifyUser
import com.cjapps.network.model.PlaylistSimple
import com.cjapps.network.model.UserPublic
import com.cjapps.network.model.Image as SpotifyImage

fun PlaylistSimple.toDomain(): Playlist {
    return Playlist(
        images = images?.map { it.toDomain() } ?: emptyList(),
        snapshotId = requireNotNull(snapshot_id),
        title = requireNotNull(name),
        urn = requireNotNull(uri),
        user = requireNotNull(owner?.toDomain())
    )
}

fun UserPublic.toDomain(): SpotifyUser {
    return SpotifyUser(
        name = requireNotNull(display_name),
        urn = requireNotNull(uri)
    )
}

fun SpotifyImage.toDomain(): Image {
    return Image(
        size = ImageSize.MEDIUM,
        url = requireNotNull(url)
    )
}