package com.example.lapordes.presentation.complaint

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.lapordes.R
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.databinding.ActivityComplaintBinding
import com.example.lapordes.databinding.ActivityComplaintDetailBinding
import com.example.lapordes.databinding.DialogAddCommentBinding
import com.example.lapordes.presentation.adapter.CommentAdapter
import com.example.lapordes.utils.IntentHelper
import com.example.lapordes.utils.ToastHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ComplaintDetailActivity : AppCompatActivity() {
    private var _binding: ActivityComplaintDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: DetailComplaintViewModel
    private lateinit var adapter: CommentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityComplaintDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this)[DetailComplaintViewModel::class.java]
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        window.statusBarColor = getColor(R.color.secondary)

        binding.btnBack.setOnClickListener {
            IntentHelper.finish(this)
        }

        val uid = intent.getStringExtra("uid")
        val title = intent.getStringExtra("title")
        val category = intent.getStringExtra("category")
        val priority = intent.getStringExtra("priority")
        val description = intent.getStringExtra("desc")
        val imageUrl = intent.getStringExtra("imageUrl")
        val lat = intent.getDoubleExtra("lat", 0.0)
        val lng = intent.getDoubleExtra("lng", 0.0)
        val status = intent.getStringExtra("status")
        val note = intent.getStringExtra("note")

        binding.tvTitle.text = title
        binding.tvStatus.text = status
        binding.tvPriority.text = priority
        binding.tvCategory.text = category
        binding.tvDescription.text = description
        binding.tvComplaintNumber.text = uid

        val createdMillis = intent.getLongExtra("created_at", 0L)
        val date = Date(createdMillis)

        val localeID = Locale("id", "ID")
        val sdf = SimpleDateFormat("EEEE, dd MMMM yyyy", localeID)
        binding.tvCreatedDate.text = sdf.format(date)

        if (status == "Proses") binding.tvStatus.setBackgroundResource(R.drawable.bg_status_process)
        if (status == "Selesai") binding.tvStatus.setBackgroundResource(R.drawable.bg_status_approved)
        if (status == "Ditolak") binding.tvStatus.setBackgroundResource(R.drawable.bg_status_rejected)

        Glide.with(this)
            .load(imageUrl)
            .into(binding.ivEvidence)

        val dialog = BottomSheetDialog(this)
        val dialogBinding = DialogAddCommentBinding.inflate(layoutInflater)

        binding.btnAddComment.setOnClickListener {
            dialog.setContentView(dialogBinding.root)

            dialogBinding.btnCancel.setOnClickListener {
                dialog.dismiss()
            }

            dialogBinding.btnSubmit.setOnClickListener {
                val commentText = dialogBinding.etComment.text.toString().trim()

                if (commentText.isEmpty()) {
                    Toast.makeText(this, "Komentar tidak boleh kosong", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val comment = dialogBinding.etComment.text.toString()
                viewModel.create(comment, uid!!, this)
                dialog.dismiss()
            }

            dialog.show()
        }

        viewModel.createState.observe(this){ state ->
            when (state) {
                is ResultState.Loading -> {
                    dialog.dismiss()
                    ToastHelper.showToast(this, "Berhasil")
                }
                is ResultState.Success -> {
                    dialog.dismiss()
                    ToastHelper.showToast(this, "Berhasil")
                }
                is ResultState.Error -> {
                    dialog.dismiss()
                    ToastHelper.showToast(this, state.message)
                }
            }
        }

        adapter = CommentAdapter()
        viewModel.get(uid!!)
        viewModel.getState.observe(this){state ->
            when(state) {
                is ResultState.Loading -> {

                }
                is ResultState.Success -> {
                    adapter.setData(state.data)
                    binding.rvComments.adapter = adapter
                    binding.tvCommentCount.text = state.data.count().toString()
                }
                is ResultState.Error -> {

                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        IntentHelper.finish(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}