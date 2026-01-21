package com.example.lapordes.presentation.customer.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.lapordes.R
import com.example.lapordes.data.local.UserPref
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.databinding.FragmentNotificationBinding
import com.example.lapordes.presentation.adapter.NotificationAdapter

class NotificationFragment : Fragment() {
    private var _binding: FragmentNotificationBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: NotificationViewModel
    private lateinit var adapter: NotificationAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this)[NotificationViewModel::class.java]

        val insetsController = WindowInsetsControllerCompat(requireActivity().window, requireActivity().window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        requireActivity().window.statusBarColor = getColor(requireContext(), R.color.secondary)

        adapter = NotificationAdapter()

        val user = UserPref(requireContext()).get()
        viewModel.get(user!!.uid)
        viewModel.getState.observe(viewLifecycleOwner){state ->
            when(state) {
                is ResultState.Loading -> {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.rvMessages.visibility = View.GONE
                }
                is ResultState.Success -> {
                    adapter.setData(state.data)
                    binding.rvMessages.adapter = adapter

                    binding.pbLoading.visibility = View.GONE
                    binding.rvMessages.visibility = View.VISIBLE
                }
                is ResultState.Error -> {

                }
            }
        }

        return binding.root
    }

}