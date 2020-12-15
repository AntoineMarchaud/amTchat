package com.amarchaud.amtchat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amarchaud.amtchat.databinding.ItemLastMessageBinding
import com.amarchaud.amtchat.viewmodel.ItemLastMessageViewModel

class LastMessagesRecyclerAdapter :
    ListAdapter<ItemLastMessageViewModel, LastMessagesRecyclerAdapter.LastMessageViewHolder>(
        DiffCallback()
    ) {

    class LastMessageViewHolder(var binding: ItemLastMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastMessageViewHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        val binding = ItemLastMessageBinding.inflate(layoutInflator, parent, false)
        binding.lastMessageLayout.setOnClickListener {
            println("go to NewMessage ?")
        }
        return LastMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LastMessageViewHolder, position: Int) {
        val contentItem: ItemLastMessageViewModel = getItem(position)
        holder.binding.itemLastMessageViewModel = contentItem
    }


    class DiffCallback : DiffUtil.ItemCallback<ItemLastMessageViewModel>() {
        override fun areItemsTheSame(
            oldItem: ItemLastMessageViewModel,
            newItem: ItemLastMessageViewModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: ItemLastMessageViewModel,
            newItem: ItemLastMessageViewModel
        ): Boolean {
            return oldItem == newItem
        }
    }


}