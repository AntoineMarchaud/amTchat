package com.amarchaud.amtchat.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.amarchaud.amtchat.R
import com.amarchaud.amtchat.databinding.ItemChatFromBinding
import com.amarchaud.amtchat.databinding.ItemChatToBinding
import com.amarchaud.amtchat.model.FirebaseChatMessageModel
import com.amarchaud.amtchat.network.FirebaseAddr
import com.amarchaud.amtchat.model.app.ItemChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ChatRecyclerAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>(
    ) {

    var context: Context? = null
    var elements: List<ItemChatViewModel> = mutableListOf()

    private val myUid = FirebaseAuth.getInstance().uid

    private var TYPE_FROM: Int = 1
    private val TYPE_TO: Int = 2

    class ChatFromViewHolder(var binding: ItemChatFromBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ChatToViewHolder(var binding: ItemChatToBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int = elements.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflator = LayoutInflater.from(parent.context)

        when (viewType) {
            TYPE_FROM ->
                return ChatFromViewHolder(
                    DataBindingUtil.inflate(
                        layoutInflator,
                        R.layout.item_chat_from,
                        parent,
                        false
                    )
                )
            else ->
                return ChatToViewHolder(
                    DataBindingUtil.inflate(
                        layoutInflator,
                        R.layout.item_chat_to,
                        parent,
                        false
                    )
                )
        }
    }

    override fun getItemViewType(position: Int): Int {
        val item = elements[position]
        if (item.firebaseChatMessageModel?.fromId == myUid)
            return TYPE_FROM
        return TYPE_TO
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        // item est le ViewModel
        val item = elements[position]

        holder.itemView.setOnLongClickListener {

            when (holder.itemViewType) {
                TYPE_FROM -> {
                    item.firebaseChatMessageModel?.let { firebaseChatMessageModel ->

                        if (!firebaseChatMessageModel.isDeleted) {

                            val holderCasted = (holder as ChatFromViewHolder)
                            if (!holderCasted.binding.displayCross) {
                                holderCasted.binding.displayCross = true;

                                /**
                                 * Manage click on Delete
                                 */
                                holderCasted.binding.crossView.setOnClickListener {
                                    holderCasted.binding.displayCross = false;

                                    /**
                                     * Update Firebase message
                                     */
                                    val fromRef = FirebaseDatabase.getInstance().getReference(
                                        FirebaseAddr.loadUserMessageForOnePerso(
                                            firebaseChatMessageModel.fromId,
                                            firebaseChatMessageModel.toId
                                        )
                                    ).child(firebaseChatMessageModel.id)

                                    val toRef = FirebaseDatabase.getInstance().getReference(
                                        FirebaseAddr.loadUserMessageForOnePerso(
                                            firebaseChatMessageModel.toId,
                                            firebaseChatMessageModel.fromId
                                        )
                                    ).child(firebaseChatMessageModel.id)

                                    firebaseChatMessageModel.text = "this message has been deleted"
                                    firebaseChatMessageModel.isDeleted = true

                                    fromRef.setValue(firebaseChatMessageModel)
                                    toRef.setValue(firebaseChatMessageModel)

                                    // todo is it useful ? when a item changed, the recycler will be called via - ChatRecyclerViewModel
                                    //notifyItemChanged(position)
                                }
                            }
                        }
                    }
                }
            }
            true
        }

        if(holder.itemViewType == TYPE_TO) {
            item.firebaseChatMessageModel?.let {
                it.isRead = true
                updateReadStatus(it)
            }
        }

        when (holder.itemViewType) {
            TYPE_FROM -> (holder as ChatFromViewHolder).binding.itemChatViewModel =
                item
            TYPE_TO -> (holder as ChatToViewHolder).binding.itemChatViewModel =
                item
        }
    }


    class DiffCallback : DiffUtil.ItemCallback<ItemChatViewModel>() {
        override fun areItemsTheSame(
            oldItem: ItemChatViewModel,
            newItem: ItemChatViewModel
        ): Boolean {
            return oldItem.firebaseChatMessageModel?.id == newItem.firebaseChatMessageModel?.id
        }

        override fun areContentsTheSame(
            oldItem: ItemChatViewModel,
            newItem: ItemChatViewModel
        ): Boolean {
            return oldItem.firebaseChatMessageModel?.text == newItem.firebaseChatMessageModel?.text && newItem.firebaseChatMessageModel?.timestamp == newItem.firebaseChatMessageModel?.timestamp
        }
    }


    private fun updateReadStatus(message: FirebaseChatMessageModel) {

        val fromRef = FirebaseDatabase.getInstance().getReference(
            FirebaseAddr.loadUserMessageForOnePerso(message.fromId, message.toId) + "/" + message.id
        )

        val toRef = FirebaseDatabase.getInstance().getReference(
            FirebaseAddr.loadUserMessageForOnePerso(message.toId, message.fromId) + "/" + message.id
        )

        message.isRead = true

        fromRef.setValue(message)
        toRef.setValue(message)

    }

}