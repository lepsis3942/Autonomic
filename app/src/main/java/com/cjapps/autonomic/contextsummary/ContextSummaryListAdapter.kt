package com.cjapps.autonomic.contextsummary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.cjapps.autonomic.R
import com.cjapps.autonomic.databinding.ContextSummaryListItemBinding
import com.cjapps.autonomic.view.toResourceId
import com.cjapps.domain.PlaybackContext

class ContextSummaryListAdapter (
    private val itemSelected: (PlaybackContext) -> Unit
) : ListAdapter<PlaybackContext, ContextSummaryListAdapter.ContextSummaryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContextSummaryViewHolder {
        val binding = ContextSummaryListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ContextSummaryViewHolder(binding, itemSelected)
    }

    override fun onBindViewHolder(holder: ContextSummaryViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

    class ContextSummaryViewHolder (
        private val binding: ContextSummaryListItemBinding,
        private val itemSelected: (PlaybackContext) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(context: PlaybackContext) {
            binding.apply {
                contextSummaryListItemTriggerText.text = context.trigger.name
                contextSummaryListItemPlaybackText.text = context.playlist.title
                contextSummaryListItemTriggerTypeImage.setImageResource(context.trigger.deviceType.toResourceId())
                val url = context.playlist.images.firstOrNull()?.url
                if (url.isNullOrEmpty()) {
                    contextSummaryListItemPlaybackAlbumArt.setImageDrawable(
                        binding.root.context.getDrawable(R.drawable.ic_album_white)
                    )
                } else {
                    contextSummaryListItemPlaybackAlbumArt.load(url) {
                        placeholder(R.drawable.ic_album_white)
                        crossfade(enable = true)
                    }
                }
                root.setOnClickListener { itemSelected(context) }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<PlaybackContext>() {
            override fun areItemsTheSame(
                oldItem: PlaybackContext,
                newItem: PlaybackContext
            ) = oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: PlaybackContext,
                newItem: PlaybackContext
            ): Boolean {
                return oldItem.playlist.urn == newItem.playlist.urn
                        && oldItem.playlist.title == newItem.playlist.title
                        && oldItem.trigger.macAddress == newItem.trigger.macAddress
                        && oldItem.trigger.name == newItem.trigger.name
                        && oldItem.shuffle == newItem.shuffle
                        && oldItem.repeat == newItem.repeat
            }
        }
    }
}