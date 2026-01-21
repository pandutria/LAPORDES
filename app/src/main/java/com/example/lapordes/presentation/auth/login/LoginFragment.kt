package com.example.lapordes.presentation.auth.login

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.lapordes.MainActivity
import com.example.lapordes.R
import com.example.lapordes.data.local.UserPref
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.databinding.FragmentLoginBinding
import com.example.lapordes.presentation.admin.AdminMainActivity
import com.example.lapordes.utils.IntentHelper
import com.example.lapordes.utils.ToastHelper

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentLoginBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginSceen_to_registerScreen)
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (email.isEmpty() || password.isEmpty())
                return@setOnClickListener ToastHelper.showToast(requireContext(), "Semua input wajib di isi!")

            viewModel.login(email, password)
        }

        viewModel.loginState.observe(viewLifecycleOwner) {state ->
            when(state) {
                is ResultState.Loading -> {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.btnLogin.visibility = View.GONE
                    binding.tvRegister.isEnabled = false
                }
                is ResultState.Success -> {
                    UserPref(requireContext()).save(state.data)
                    if (state.data.admin) {
                        IntentHelper.navigate(requireActivity(), AdminMainActivity::class.java)
                    } else {
                        IntentHelper.navigate(requireActivity(), MainActivity::class.java)
                    }

                    ToastHelper.showToast(requireContext(), "Berhasil masuk akun!")
                    IntentHelper.finish(requireActivity())
                }
                is ResultState.Error -> {
                    if (!isAdded) return@observe
                    binding.pbLoading.visibility = View.GONE
                    binding.btnLogin.visibility = View.VISIBLE
                    binding.tvRegister.isEnabled = true

                    ToastHelper.showToast(requireContext(), state.message)
                    Log.d("firebaseErr", state.message)
                }
            }
        }

        return binding.root
    }
}