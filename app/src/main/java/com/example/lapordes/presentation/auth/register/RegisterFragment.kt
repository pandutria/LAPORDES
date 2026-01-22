package com.example.lapordes.presentation.auth.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.lapordes.R
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.databinding.FragmentLoginBinding
import com.example.lapordes.databinding.FragmentRegisterBinding
import com.example.lapordes.utils.FirebaseHelper
import com.example.lapordes.utils.ToastHelper
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.launch

class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: RegisterViewModel

    //    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private var googleEmail: String? = null

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
                return@setOnClickListener ToastHelper.showToast(
                    requireContext(),
                    "Semua input wajib di isi!"
                )

            viewModel.register(email, password)
        }

        viewModel.registerState.observe(viewLifecycleOwner) { state ->
            when (state) {
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

        binding.btnGoogleLogin.setOnClickListener {
            pickGoogleEmail()
        }


        return binding.root
    }

    private fun pickGoogleEmail() {
        val credentialManager = CredentialManager.create(requireActivity())

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.default_web_client_id))
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = requireActivity(),
                    request = request
                )

                val googleCredential =
                    GoogleIdTokenCredential.createFrom(result.credential.data)

                googleEmail = googleCredential.id

                binding.etEmail.setText(googleEmail)
                binding.etEmail.isEnabled = false
                binding.etEmail.alpha = 0.6f

                ToastHelper.showToast(
                    requireContext(),
                    "Email Google dipilih"
                )

            } catch (e: Exception) {
                Log.e("GooglePick", "ERROR", e)
                ToastHelper.showToast(
                    requireContext(),
                    "Pilih akun Google dibatalkan"
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}