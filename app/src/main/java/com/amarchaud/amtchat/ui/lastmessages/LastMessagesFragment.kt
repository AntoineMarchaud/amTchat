package com.amarchaud.amtchat.ui.lastmessages

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.amarchaud.amtchat.R
import com.amarchaud.amtchat.adapter.LastMessagesRecyclerAdapter
import com.amarchaud.amtchat.databinding.LastMessagesFragmentBinding
import com.amarchaud.amtchat.service.MessageService
import com.amarchaud.amtchat.viewmodel.ItemLastMessageViewModel
import com.google.firebase.auth.FirebaseAuth


class LastMessagesFragment : Fragment() {

    companion object {
        fun newInstance() = LastMessagesFragment()
    }

    private lateinit var binding: LastMessagesFragmentBinding
    private val viewModel: LastMessagesViewModel by viewModels()

    // recycler view
    private var lastMessagesRecyclerAdapter: LastMessagesRecyclerAdapter =
        LastMessagesRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LastMessagesFragmentBinding.inflate(inflater, container, false)

        // for actionBar only
        (activity as AppCompatActivity).supportActionBar?.show()
        setHasOptionsMenu(true)

        // for toolbar only :
        /*
        (activity as AppCompatActivity).toolbar?.inflateMenu(R.menu.nav_menu)
        (activity as AppCompatActivity).toolbar?.setOnMenuItemClickListener { item ->
            item?.let {
                proceedItemMenuClicked(item)
            }
            true
        }*/
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
            lastMessagesRecyclerAdapter.notifyDataSetChanged()
        }
    }


    /**
     *  For ACTIONBAR ! (not toolbar)
     */

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.nav_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        proceedItemMenuClicked(item)
        return super.onOptionsItemSelected(item)


        /*
        // AndroidX Navigation : Bug ----> when go back to CreateAccount
        // if user pressed onBack button, the LastMessagesFragment is displayed again !
        // todo how to fix it ?
        if(item.itemId == R.id.createAccountFragment) {
            FirebaseAuth.getInstance().signOut()
        }

        val navController = findNavController(requireView())
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)*/
    }


    private fun proceedItemMenuClicked(item: MenuItem) {
        when (item.itemId) {
            R.id.newMessageFragment -> {
                val action =
                    LastMessagesFragmentDirections.actionLastMessagesFragmentToNewMessageFragment()
                findNavController(requireView()).navigate(action)
            }
            R.id.createAccountFragment -> {
                FirebaseAuth.getInstance().signOut()

                // stop service
                Intent(this@LastMessagesFragment.context, MessageService::class.java).also { intent ->
                    this@LastMessagesFragment.context?.stopService(intent)
                }

                val action =
                    LastMessagesFragmentDirections.actionLastMessagesFragmentToCreateAccountFragment()
                findNavController(requireView()).navigate(action)
            }
        }
    }

}