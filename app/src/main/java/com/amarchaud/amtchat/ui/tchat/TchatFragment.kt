package com.amarchaud.amtchat.ui.tchat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.amarchaud.amtchat.adapter.ChatRecyclerAdapter
import com.amarchaud.amtchat.databinding.FragmentTchatBinding
import com.amarchaud.amtchat.injection.ViewModelFactory

class TchatFragment : Fragment() {

    companion object {
        fun newInstance() = TchatFragment()
        const val TAG = "ChatFragment"
    }

    // arguments passés avec Navigation Component
    val args: TchatFragmentArgs by navArgs()

    // recycler view
    private var chatRecyclerAdapter: ChatRecyclerAdapter = ChatRecyclerAdapter()

    private lateinit var binding: FragmentTchatBinding
    private val viewModel: TchatViewModel by viewModels {
        ViewModelFactory(
            activity?.application!!,
            args.ChatUser
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar?.title = args.ChatUser.username

        chatRecyclerAdapter.context = this.context

        binding = FragmentTchatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chatViewModel = viewModel

        binding.recyclerviewChatLog.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewChatLog.adapter = chatRecyclerAdapter

        /**
         * Appeler a l'update / ajout / destruction d'un message
         */
        viewModel.listOfMessagesLiveData.observe(viewLifecycleOwner, {
            chatRecyclerAdapter.elements = it.first

            when (it.second) {
                TchatViewModel.Companion.typeItem.ITEM_INSERTED -> {
                    chatRecyclerAdapter.notifyItemInserted(it.third)
                    binding.recyclerviewChatLog.scrollToPosition(it.third)
                }
                TchatViewModel.Companion.typeItem.ITEM_MODIFIED -> chatRecyclerAdapter.notifyItemChanged(
                    it.third
                )
                TchatViewModel.Companion.typeItem.ITEM_DELETED -> chatRecyclerAdapter.notifyItemRemoved(
                    it.third
                )
            }
        })
    }


    override fun onStart() {
        super.onStart()
        viewModel.onStart()
    }

    override fun onStop() {
        super.onStop()
        viewModel.onStop()
    }
}