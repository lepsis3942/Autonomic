package com.cjapps.autonomic.bridge

import com.squareup.moshi.JsonClass

/**
 * Information needed to initiate playback on Spotify
 * Created by cjgonz on 2020-03-22.
 */
@JsonClass(generateAdapter = true)
data class PlaybackInfo(
    val playbackUri: String,
    val playbackOptions: PlaybackOptions
)

@JsonClass(generateAdapter = true)
data class PlaybackOptions(val shuffle: Boolean = false, val repeatTimes: Int = -1)