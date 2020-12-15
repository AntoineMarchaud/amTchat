package com.amarchaud.amtchat.ui.createaccount

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import com.amarchaud.amtchat.databinding.CreateAccountFragmentBinding

class CreateAccountFragment : Fragment() {

    companion object {
        fun newInstance() = CreateAccountFragment()
    }

    // Returned when user has selected a photo
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.onSelectedPhoto(it)
    }

    private lateinit var binding: CreateAccountFragmentBinding
    private lateinit var viewModel: CreateAccountViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar?.show()
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)

        binding = CreateAccountFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(CreateAccountViewModel::class.java)
        binding.createAccountViewModel = viewModel

        viewModel.actionToNextScreen.observe(viewLifecycleOwner, {
            Navigation.findNavController(view).navigate(it)
        })

        viewModel.pickPhotoAction.observe(viewLifecycleOwner, {
            // Pass in the mime type you'd like to allow the user to select
            // as the input
            getContent.launch("image/*")
        })
    }
}