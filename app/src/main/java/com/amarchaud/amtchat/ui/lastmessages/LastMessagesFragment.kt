package com.amarchaud.amtchat.ui.lastmessages

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.recyclerview.widget.LinearLayoutManager
import com.amarchaud.amtchat.MainActivity
import com.amarchaud.amtchat.R
import com.amarchaud.amtchat.adapter.LastMessagesRecyclerAdapter
import com.amarchaud.amtchat.databinding.LastMessagesFragmentBinding
import com.amarchaud.amtchat.viewmodel.ItemLastMessageViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*


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
    }

    private fun proceedItemMenuClicked(item: MenuItem) {

        // AndroidX Navigation
        /*
        if(item.itemId == R.id.createAccountFragment) {
            FirebaseAuth.getInstance().signOut()
        }

        val navController = findNavController(requireView())
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
        */

        when (item.itemId) {
            R.id.newMessageFragment -> {
                val action =
                    LastMessagesFragmentDirections.actionLastMessagesFragmentToNewMessageFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }
            R.id.createAccountFragment -> {
                FirebaseAuth.getInstance().signOut()

                val action =
                    LastMessagesFragmentDirections.actionLastMessagesFragmentToCreateAccountFragment()
                Navigation.findNavController(requireView()).navigate(action)
            }
        }
    }

}