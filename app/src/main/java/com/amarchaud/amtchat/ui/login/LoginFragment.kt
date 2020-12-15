package com.amarchaud.amtchat.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.amarchaud.amtchat.databinding.LoginFragmentBinding

class LoginFragment : Fragment() {

    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var binding: LoginFragmentBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        (activity as AppCompatActivity).supportActionBar?.title = "Login"

        binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.loginViewModel = viewModel

        viewModel.actionLiveData.observe(viewLifecycleOwner, {
            Navigation.findNavController(view).navigate(it)
        })
    }
}