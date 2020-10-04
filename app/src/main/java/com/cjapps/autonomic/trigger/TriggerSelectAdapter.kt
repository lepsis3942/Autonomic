package com.cjapps.autonomic.trigger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cjapps.autonomic.databinding.TriggerSelectListItemBinding
import com.cjapps.autonomic.view.toResourceId
import com.cjapps.domain.Trigger

class TriggerSelectAdapter(
    private val itemSelected: (Trigger) -> Unit
) : ListAdapter<Trigger, TriggerSelectAdapter.TriggerSelectDevice>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TriggerSelectDevice {
        val binding = TriggerSelectListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return TriggerSelectDevice(binding, itemSelected)
    }

    override fun onBindViewHolder(holder: TriggerSelectDevice, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    private object DiffCallback : DiffUtil.ItemCallback<Trigger>() {
        override fun areItemsTheSame(
            oldItem: Trigger,
            newItem: Trigger
        ): Boolean {
            return oldItem.macAddress == newItem.macAddress
        }

        override fun areContentsTheSame(
            oldItem: Trigger,
            newItem: Trigger
        ): Boolean {
            // Object equals is sufficient here
            return oldItem == newItem
        }

    }

    class TriggerSelectDevice(
        private val binding: TriggerSelectListItemBinding,
        private val itemSelected: (Trigger) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(device: Trigger) {
            binding.triggerSelectListImage.setImageResource(device.deviceType.toResourceId())
            binding.triggerSelectListName.text = device.name
            binding.root.setOnClickListener { itemSelected(device) }
        }
    }
}