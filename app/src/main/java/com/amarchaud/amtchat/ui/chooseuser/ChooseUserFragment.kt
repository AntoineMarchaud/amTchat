package com.amarchaud.amtchat.ui.chooseuser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.amarchaud.amtchat.adapter.ChooseUserRecyclerAdapter
import com.amarchaud.amtchat.databinding.FragmentChooseUserBinding
import com.amarchaud.amtchat.model.FirebaseUserModel

class ChooseUserFragment : Fragment() {

    // recycler view
    private var chooseUserRecyclerAdapter: ChooseUserRecyclerAdapter = ChooseUserRecyclerAdapter()

    private lateinit var binding: FragmentChooseUserBinding
    private lateinit var viewModel: ChooseUserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(ChooseUserViewModel::class.java)

        binding.recyclerviewChooseUser.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        binding.recyclerviewChooseUser.adapter = chooseUserRecyclerAdapter

        viewModel.listOfUsersLiveData.observe(viewLifecycleOwner, { users: List<FirebaseUserModel> ->
            chooseUserRecyclerAdapter.submitList(users)
        })
    }
}