package com.example.lapordes.presentation.customer.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.example.lapordes.R
import com.example.lapordes.data.model.Complaint
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.databinding.FragmentHomeBinding
import com.example.lapordes.presentation.adapter.ComplaintAdapter
import com.example.lapordes.presentation.complaint.ComplaintDetailActivity
import com.example.lapordes.presentation.customer.complaint.ComplaintActivity
import com.example.lapordes.utils.IntentHelper

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: HomeViewModel
    private lateinit var adapter: ComplaintAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        adapter = ComplaintAdapter{ complaint ->
            val bundle = Bundle().apply {
                putString("uid", complaint.uid)
                putString("title", complaint.title)
                putString("category", complaint.category)
                putString("priority", complaint.priority)
                putString("desc", complaint.description)
                putString("imageUrl", complaint.imageUrl)
                putDouble("lat", complaint.lat)
                putDouble("lng", complaint.lng)
                putString("status", complaint.status)
                putString("note", complaint.note)
                putLong("created_at", complaint.created_at!!.seconds * 1000)
            }

            IntentHelper.navigate(requireActivity(), ComplaintDetailActivity::class.java, bundle)
        }

        binding.btnCreateComplaint.setOnClickListener {
            IntentHelper.navigate(requireActivity(), ComplaintActivity::class.java)
        }

        viewModel.get(requireContext())
        viewModel.getState.observe(viewLifecycleOwner){state ->
            when(state) {
                is ResultState.Loading -> {
                    binding.tvTotalProses.text = "0"
                    binding.tvTotalDitolak.text = "0"
                    binding.tvTotalBerhasil.text = "0"
                    binding.tvTotalPengaduan.text = "0"

                    binding.pbLoading.visibility = View.VISIBLE
                    binding.rvPengaduan.visibility = View.GONE
                }
                is ResultState.Success -> {
                    adapter.setData(state.data)
                    binding.rvPengaduan.adapter = adapter

                    binding.tvTotalProses.text = state.data.count{x -> x.status == "Proses"}.toString()
                    binding.tvTotalBerhasil.text = state.data.count{x -> x.status == "Selesai"}.toString()
                    binding.tvTotalDitolak.text = state.data.count{x -> x.status == "DiTolak"}.toString()
                    binding.tvTotalPengaduan.text = state.data.count().toString()

                    binding.pbLoading.visibility = View.GONE
                    binding.tvEmpty.visibility = View.GONE
                    binding.rvPengaduan.visibility = View.VISIBLE

                    if (state.data.isEmpty()) {
                        binding.rvPengaduan.visibility = View.GONE
                        binding.tvEmpty.visibility = View.VISIBLE
                    }
                }
                is ResultState.Error -> {
                    viewModel.get(requireContext())
                }
            }
        }

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        viewModel.get(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}