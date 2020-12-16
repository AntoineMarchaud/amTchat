package com.amarchaud.amtchat.ui.lastmessages

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.amarchaud.amtchat.MainActivity
import com.amarchaud.amtchat.R
import com.amarchaud.amtchat.adapter.LastMessagesRecyclerAdapter
import com.amarchaud.amtchat.databinding.LastMessagesFragmentBinding
import com.amarchaud.amtchat.viewmodel.ItemLastMessageViewModel
import com.google.firebase.auth.FirebaseAuth


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

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        setHasOptionsMenu(true)

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

        (requireActivity() as MainActivity).getStackInfos()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_new_message -> {
                val action =
                    LastMessagesFragmentDirections.actionLastMessagesFragmentToNewMessageFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()

                val action =
                    LastMessagesFragmentDirections.actionLastMessagesFragmentToCreateAccountFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}