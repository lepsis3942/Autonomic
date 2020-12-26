package com.cjapps.persistence.mapper

import com.cjapps.domain.PlaybackContext
import com.cjapps.domain.Repeat
import com.cjapps.persistence.entity.Context
import com.cjapps.persistence.entity.RepeatType
import com.cjapps.persistence.entity.FullContext as EntityContext

internal fun EntityContext.toDomain(): PlaybackContext {
    return PlaybackContext(
        id = context.id,
        playlist = playlistAndImages.toDomain(),
        repeat = context.repeat.toDomain(),
        shuffle = context.shuffle,
        trigger = trigger.toDomain()
    )
}

internal fun PlaybackContext.toEntity(): EntityContext {
    return EntityContext(
        context = Context(
            id = id,
            repeat = repeat.toEntity(),
            shuffle = shuffle
        ),
        playlistAndImages = playlist.toEntity(id),
        trigger = trigger.toEntity(id)
    )
}

internal fun RepeatType.toDomain(): Repeat {
    return when (this) {
        RepeatType.NONE -> Repeat.NONE
        RepeatType.ALL -> Repeat.ALL
        RepeatType.ONCE -> Repeat.ONCE
    }
}

internal fun Repeat.toEntity(): RepeatType {
    return when (this) {
        Repeat.NONE -> RepeatType.NONE
        Repeat.ALL -> RepeatType.ALL
        Repeat.ONCE -> RepeatType.ONCE
    }
}