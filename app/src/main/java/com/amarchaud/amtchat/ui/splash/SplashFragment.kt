package com.amarchaud.amtchat.ui.splash

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.amarchaud.amtchat.databinding.SplashFragmentBinding

class SplashFragment : Fragment() {

    companion object {
        fun newInstance() = SplashFragment()
    }

    private lateinit var binding: SplashFragmentBinding
    private lateinit var viewModel: SplashViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SplashFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)


        viewModel.actionLiveData.observe(viewLifecycleOwner, {
            Navigation.findNavController(view).navigate(it)
        })
    }
}