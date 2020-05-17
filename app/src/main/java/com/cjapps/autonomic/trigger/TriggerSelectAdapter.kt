package com.cjapps.autonomic.trigger

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cjapps.autonomic.R
import com.cjapps.autonomic.databinding.TriggerSelectListItemBinding
import com.cjapps.domain.Trigger
import com.cjapps.domain.TriggerDeviceType

class TriggerSelectAdapter(
    private val itemSelected: (Trigger) -> Unit
): ListAdapter<Trigger, TriggerSelectAdapter.TriggerSelectDevice>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TriggerSelectDevice {
        val binding = TriggerSelectListItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false)
        return TriggerSelectDevice(binding, itemSelected)
    }

    override fun onBindViewHolder(holder: TriggerSelectDevice, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    private object DiffCallback: DiffUtil.ItemCallback<Trigger>() {
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
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(device: Trigger) {
            binding.triggerSelectListImage.setImageResource(imageIdForDeviceType(device.deviceType))
            binding.triggerSelectListName.text = device.name
            binding.root.setOnClickListener { itemSelected(device) }
        }

        private fun imageIdForDeviceType(deviceType: TriggerDeviceType): Int {
            return when (deviceType) {
                TriggerDeviceType.BLUETOOTH_VEHICLE_AUDIO -> R.drawable.ic_car_black
                TriggerDeviceType.BLUETOOTH_AUDIO -> R.drawable.ic_headset_black
                TriggerDeviceType.BLUETOOTH_COMPUTER -> R.drawable.ic_computer_black
                TriggerDeviceType.BLUETOOTH_WEARABLE -> R.drawable.ic_devices_other_black
                TriggerDeviceType.BLUETOOTH_GENERIC -> R.drawable.ic_bluetooth_black
            }
        }
    }
}