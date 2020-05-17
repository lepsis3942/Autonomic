package com.cjapps.domain

data class Playlist(
    var id: Long = 0,
    var images: List<Image>,
    var snapshotId: String,
    var user: SpotifyUser,
    var urn: String
)