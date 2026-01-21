package com.example.lapordes.presentation.customer.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.example.lapordes.R
import com.example.lapordes.data.local.UserPref
import com.example.lapordes.databinding.FragmentProfileBinding
import com.example.lapordes.presentation.auth.AuthActivity
import com.example.lapordes.utils.IntentHelper

class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentProfileBinding.inflate(layoutInflater, container, false)
        val insetsController = WindowInsetsControllerCompat(requireActivity().window, requireActivity().window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        requireActivity().window.statusBarColor = getColor(requireContext(), R.color.secondary)

        binding.tvEmail.text = UserPref(requireContext()).get()!!.email

        binding.layoutExit.setOnClickListener {
            IntentHelper.navigate(requireActivity(), AuthActivity::class.java)
            IntentHelper.finish(requireActivity())
        }

        binding.layoutExit.setOnClickListener {
            requireActivity().finishAffinity()
        }

        return binding.root
    }
}