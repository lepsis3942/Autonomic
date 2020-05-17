package com.cjapps.domain

data class PlaybackContext(
    var id: Long = 0,
    var playlist: Playlist,
    var repeat: Boolean,
    var shuffle: Boolean,
    var trigger: Trigger
)