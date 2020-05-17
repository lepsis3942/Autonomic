package com.cjapps.persistence.mapper

import com.cjapps.domain.PlaybackContext
import com.cjapps.persistence.entity.Context
import com.cjapps.persistence.entity.FullContext as EntityContext

internal fun EntityContext.toDomain(): PlaybackContext {
    return PlaybackContext(
        id = context.id,
        playlist = playlistAndImages.toDomain(),
        repeat = context.repeat,
        shuffle = context.shuffle,
        trigger = trigger.toDomain()
    )
}

internal fun PlaybackContext.toEntity(): EntityContext {
    return EntityContext(
        context = Context(
            id = id,
            repeat = repeat,
            shuffle = shuffle
        ),
        playlistAndImages = playlist.toEntity(id),
        trigger = trigger.toEntity(id)
    )
}