package com.example.lapordes.presentation.auth.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lapordes.MainActivity
import com.example.lapordes.R
import com.example.lapordes.databinding.FragmentLoginBinding
import com.example.lapordes.utils.IntentHelper

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(layoutInflater)

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginSceen_to_registerScreen)
        }

        binding.btnLogin.setOnClickListener {
            IntentHelper.navigate(requireActivity(), MainActivity::class.java)
        }

        return binding.root
    }
}