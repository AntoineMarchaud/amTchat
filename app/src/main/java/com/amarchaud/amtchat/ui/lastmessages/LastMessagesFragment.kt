package com.amarchaud.amtchat.ui.lastmessages

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.amarchaud.amtchat.adapter.LastMessagesRecyclerAdapter
import com.amarchaud.amtchat.databinding.LastMessagesFragmentBinding
import com.amarchaud.amtchat.viewmodel.ItemLastMessageViewModel

class LastMessagesFragment : Fragment() {

    companion object {
        fun newInstance() = LastMessagesFragment()
    }

    private lateinit var binding: LastMessagesFragmentBinding
    private lateinit var viewModel: LastMessagesViewModel

    // recycler view
    private var lastMessagesRecyclerAdapter: LastMessagesRecyclerAdapter =
        LastMessagesRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar?.title = "Mes messages"

        binding = LastMessagesFragmentBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(LastMessagesViewModel::class.java)

        binding.lastMessagesRecyclerView.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.lastMessagesRecyclerView.adapter = lastMessagesRecyclerAdapter

        binding.newMessage.setOnClickListener {
            val action =
                LastMessagesFragmentDirections.actionLastMessagesFragmentToNewMessageFragment()
            Navigation.findNavController(view).navigate(action)
        }

        viewModel.listOfLastMessagesLiveData.observe(viewLifecycleOwner) { users: List<ItemLastMessageViewModel> ->
            lastMessagesRecyclerAdapter.submitList(users)
        }
        viewModel.MyselfLiveData.observe(viewLifecycleOwner, {
            lastMessagesRecyclerAdapter.Myself = it
        })
    }
}