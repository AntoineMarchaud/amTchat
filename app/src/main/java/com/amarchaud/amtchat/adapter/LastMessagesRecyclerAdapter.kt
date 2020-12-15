package com.amarchaud.amtchat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amarchaud.amtchat.R
import com.amarchaud.amtchat.databinding.ItemLastMessageBinding
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.amarchaud.amtchat.ui.chooseuser.ChooseUserFragmentDirections
import com.amarchaud.amtchat.ui.lastmessages.LastMessagesFragmentDirections
import com.amarchaud.amtchat.viewmodel.ItemLastMessageViewModel

class LastMessagesRecyclerAdapter :
    ListAdapter<ItemLastMessageViewModel, LastMessagesRecyclerAdapter.LastMessageViewHolder>(
        DiffCallback()
    ) {

    lateinit var Myself : FirebaseUserModel

    class LastMessageViewHolder(var binding: ItemLastMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LastMessageViewHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        val binding = ItemLastMessageBinding.inflate(layoutInflator, parent, false)

        return LastMessagesRecyclerAdapter.LastMessageViewHolder(
            DataBindingUtil.inflate(
                layoutInflator,
                R.layout.item_last_message,
                parent,
                false
            ))
    }

    override fun onBindViewHolder(holder: LastMessageViewHolder, position: Int) {
        val contentItem: ItemLastMessageViewModel = getItem(position)
        holder.binding.itemLastMessageViewModel = contentItem

        holder.itemView.setOnClickListener {
            // passage a l'écran chat
            val action = LastMessagesFragmentDirections.actionLastMessagesFragmentToChatFragment(Myself, contentItem.lastConvUser)
            Navigation.findNavController(it).navigate(action)
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<ItemLastMessageViewModel>() {
        override fun areItemsTheSame(
            oldItem: ItemLastMessageViewModel,
            newItem: ItemLastMessageViewModel
        ): Boolean {
            return oldItem.lastConvChat.id == newItem.lastConvChat.id
        }

        override fun areContentsTheSame(
            oldItem: ItemLastMessageViewModel,
            newItem: ItemLastMessageViewModel
        ): Boolean {
            return oldItem == newItem
        }
    }


}