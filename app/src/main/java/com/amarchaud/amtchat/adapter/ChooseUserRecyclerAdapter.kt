package com.amarchaud.amtchat.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.amarchaud.amtchat.databinding.ItemNewMessageBinding
import com.amarchaud.amtchat.model.FirebaseUserModel
import com.amarchaud.amtchat.ui.chooseuser.ChooseUserFragmentDirections

class ChooseUserRecyclerAdapter :
    ListAdapter<FirebaseUserModel, ChooseUserRecyclerAdapter.NewMessageViewHolder>(
        DiffCallback()
    ) {

    lateinit var Myself : FirebaseUserModel

    class NewMessageViewHolder(var binding: ItemNewMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewMessageViewHolder {
        val layoutInflator = LayoutInflater.from(parent.context)
        val binding = ItemNewMessageBinding.inflate(layoutInflator, parent, false)
        return NewMessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewMessageViewHolder, position: Int) {
        val ToUser: FirebaseUserModel = getItem(position)
        holder.binding.firebaseUser = ToUser

        // passage a l'écran Chat !
        holder.itemView.setOnClickListener {
            val action = ChooseUserFragmentDirections.actionNewMessageFragmentToChatFragment(Myself, ToUser)
            Navigation.findNavController(it).navigate(action)
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<FirebaseUserModel>() {
        override fun areItemsTheSame(
            oldItem: FirebaseUserModel,
            newItem: FirebaseUserModel
        ): Boolean {
            return oldItem.uid == newItem.uid
        }

        override fun areContentsTheSame(
            oldItem: FirebaseUserModel,
            newItem: FirebaseUserModel
        ): Boolean {
            return oldItem.profileImageUrl == newItem.profileImageUrl && newItem.username == newItem.username
        }
    }
}