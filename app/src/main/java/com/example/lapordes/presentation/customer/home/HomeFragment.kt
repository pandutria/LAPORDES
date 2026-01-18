package com.example.lapordes.presentation.customer.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.lapordes.R
import com.example.lapordes.databinding.FragmentHomeBinding
import com.example.lapordes.presentation.customer.complaint.ComplaintActivity
import com.example.lapordes.utils.IntentHelper

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater)

        binding.btnCreateComplaint.setOnClickListener {
            IntentHelper.navigate(requireActivity(), ComplaintActivity::class.java)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}