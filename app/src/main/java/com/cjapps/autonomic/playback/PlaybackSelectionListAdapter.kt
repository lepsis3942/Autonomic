package com.cjapps.autonomic.playback

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.cjapps.autonomic.R
import com.cjapps.autonomic.databinding.PlaybackSelectListItemBinding
import com.cjapps.autonomic.view.ViewConstants
import com.cjapps.network.model.PlaylistSimple

class PlaybackSelectionListAdapter(
    private val itemSelected: (PlaylistSimple) -> Unit,
    private val endOfAdapterReached: () -> Unit
) :
    ListAdapter<PlaylistSimple, PlaybackSelectionListAdapter.PlaybackSelectionViewHolder>(
        DIFF_CALLBACK
    ) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaybackSelectionViewHolder {
        val binding = PlaybackSelectListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PlaybackSelectionViewHolder(binding, itemSelected)
    }

    override fun onBindViewHolder(holder: PlaybackSelectionViewHolder, position: Int) {
        if (position == itemCount - 1) endOfAdapterReached()
        getItem(position)?.let { holder.bind(it) }
    }


    class PlaybackSelectionViewHolder(
        private val binding: PlaybackSelectListItemBinding,
        private val itemSelected: (PlaylistSimple) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private var albumDrawableResource: Drawable? = null

        fun bind(playlist: PlaylistSimple) {
            binding.apply {
                playbackSelectListMusicTitle.text = playlist.name
                playbackSelectListCreatorTitle.text = playlist.owner?.display_name ?: ""
                val imageUrl = playlist.images?.firstOrNull()?.url
                if (imageUrl.isNullOrBlank()) {
                    playbackSelectListAlbumArt.setImageDrawable(getAlbumDrawable())
                } else {
                    playbackSelectListAlbumArt.load(imageUrl) {
                        placeholder(R.drawable.ic_album_black)
                        crossfade(enable = true)
                        transformations(*ViewConstants.IMAGE_VIEW_TRANSFORMATIONS)
                    }
                }
                root.setOnClickListener { itemSelected(playlist) }
            }
        }

        private fun getAlbumDrawable(): Drawable? {
            return if (albumDrawableResource == null) {
                binding.root.context.getDrawable(R.drawable.ic_album_black)
            } else {
                albumDrawableResource
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PlaylistSimple>() {
            override fun areItemsTheSame(
                oldItem: PlaylistSimple,
                newItem: PlaylistSimple
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: PlaylistSimple,
                newItem: PlaylistSimple
            ): Boolean {
                // Snapshot is Spotify's record of change on data
                return oldItem.snapshot_id == newItem.snapshot_id
            }
        }
    }
}