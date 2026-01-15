package com.example.lapordes.presentation.auth.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.lapordes.R
import com.example.lapordes.databinding.FragmentLoginBinding
import com.example.lapordes.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(layoutInflater)

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_registerScreen_to_loginSceen)
        }

        return binding.root
    }
}