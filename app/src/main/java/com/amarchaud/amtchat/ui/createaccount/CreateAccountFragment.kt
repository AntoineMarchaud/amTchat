package com.amarchaud.amtchat.ui.createaccount

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.amarchaud.amtchat.databinding.FragmentCreateAccountBinding

class CreateAccountFragment : Fragment() {

    // Returned when user has selected a photo
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.onSelectedPhoto(it)
    }

    private lateinit var binding: FragmentCreateAccountBinding
    private val viewModel: CreateAccountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar?.show()

        binding = FragmentCreateAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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