package com.cjapps.network.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaylistSimple(
    var tracks: PlaylistTracksInformation? = null,
    var collaborative: Boolean? = null,
    var external_urls: Map<String, String>? = null,
    var href: String? = null,
    var id: String? = null,
    var images: List<Image>? = null,
    var name: String? = null,
    var owner: UserPublic? = null,
    @SerializedName("public")
    var is_public: Boolean? = null,
    var snapshot_id: String? = null,
    var type: String? = null,
    var uri: String? = null
) : Parcelable