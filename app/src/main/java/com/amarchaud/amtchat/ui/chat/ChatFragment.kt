package com.amarchaud.amtchat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.amarchaud.amtchat.adapter.ChatRecyclerAdapter
import com.amarchaud.amtchat.databinding.ChatFragmentBinding
import com.amarchaud.amtchat.injection.ViewModelFactory

class ChatFragment : Fragment() {

    companion object {
        fun newInstance() = ChatFragment()
        const val TAG = "ChatFragment"
    }

    // arguments passés avec Navigation Component
    val args: ChatFragmentArgs by navArgs()

    // recycler view
    private var chatRecyclerAdapter: ChatRecyclerAdapter = ChatRecyclerAdapter()

    private lateinit var binding: ChatFragmentBinding
    private lateinit var viewModel: ChatViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar?.title = args.ChatUser.username

        binding = ChatFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(activity?.application!!, args.MyselfUser, args.ChatUser)
        ).get(ChatViewModel::class.java)
        binding.chatViewModel = viewModel

        binding.recyclerviewChatLog.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewChatLog.adapter = chatRecyclerAdapter

        /**
         * Appelé a chaque nouveau message
         */
        viewModel.listOfMessagesLiveData.observe(viewLifecycleOwner, {
            chatRecyclerAdapter.elements = it.first

            when (it.second) {
                ChatViewModel.Companion.typeItem.ITEM_INSERTED -> {
                    chatRecyclerAdapter.notifyItemInserted(it.third)
                    binding.recyclerviewChatLog.scrollToPosition(it.third)
                }
                ChatViewModel.Companion.typeItem.ITEM_MODIFIED -> chatRecyclerAdapter.notifyItemChanged(
                    it.third
                )
                ChatViewModel.Companion.typeItem.ITEM_DELETED -> chatRecyclerAdapter.notifyItemRemoved(
                    it.third
                )
            }
        })
    }

}