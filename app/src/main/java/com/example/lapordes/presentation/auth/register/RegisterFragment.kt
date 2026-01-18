package com.example.lapordes.presentation.auth.register

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lapordes.R
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.databinding.FragmentLoginBinding
import com.example.lapordes.databinding.FragmentRegisterBinding
import com.example.lapordes.utils.ToastHelper

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RegisterViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[RegisterViewModel::class.java]

        binding.tvLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerScreen_to_loginSceen)
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty())
                return@setOnClickListener ToastHelper.showToast(requireContext(), "Semua input wajib di isi!")

            viewModel.register(email, password)
        }

        viewModel.registerState.observe(viewLifecycleOwner){state ->
            when(state) {
                is ResultState.Loading -> {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.btnRegister.visibility = View.GONE
                    binding.tvLogin.isEnabled = false
                }
                is ResultState.Success -> {
                    findNavController().navigate(R.id.action_registerScreen_to_loginSceen)
                    ToastHelper.showToast(requireContext(), "Berhasil membuat akun!")
                }
                is ResultState.Error -> {
                    if (!isAdded) return@observe
                    binding.pbLoading.visibility = View.GONE
                    binding.btnRegister.visibility = View.VISIBLE
                    binding.tvLogin.isEnabled = true

                    ToastHelper.showToast(requireContext(), state.message)
                    Log.d("firebaseErr", state.message)
                }
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}