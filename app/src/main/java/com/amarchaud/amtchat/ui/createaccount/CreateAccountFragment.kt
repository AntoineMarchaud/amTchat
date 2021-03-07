package com.amarchaud.amtchat.ui.createaccount

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.amarchaud.amtchat.R
import com.amarchaud.amtchat.databinding.FragmentCreateAccountBinding
import com.bumptech.glide.Glide


class CreateAccountFragment : Fragment() {

    // Returned when user has selected a photo
    // jetpack compose (alpha)
    /*
    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
        viewModel.onSelectedPhoto(it)
    }
     */

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

        with(binding) {
            selectphotoButtonRegister.setOnClickListener {
                val photoPickerIntent = Intent(Intent.ACTION_PICK)
                photoPickerIntent.type = "image/*"
                startActivityForResult(photoPickerIntent, 100)
            }
        }

        viewModel.actionToNextScreen.observe(viewLifecycleOwner, {
            Navigation.findNavController(view).navigate(it)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 100) {
            if (resultCode == RESULT_OK) {
                // update view model
                viewModel.onSelectedPhoto(data?.data)

                // update ui
                try {
                    Glide.with(requireContext())
                        .load(data?.data)
                        .error(R.drawable.common_full_open_on_phone)
                        .into(binding.selectphotoButtonRegister)
                } catch (e: IllegalArgumentException) {
                    binding.selectphotoButtonRegister.setImageResource(R.drawable.common_full_open_on_phone)
                }
            } else {
                Toast.makeText(requireContext(), "You haven't picked Image", Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}