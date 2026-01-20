package com.example.lapordes.presentation.complaint

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.lapordes.R
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.databinding.ActivityComplaintDetailBinding
import com.example.lapordes.databinding.DialogAddCommentBinding
import com.example.lapordes.databinding.DialogChangeStatusBinding
import com.example.lapordes.presentation.adapter.CommentAdapter
import com.example.lapordes.utils.IntentHelper
import com.example.lapordes.utils.ToastHelper
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ComplaintDetailActivity : AppCompatActivity() {
    private var _binding: ActivityComplaintDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ComplaintDetailViewModel
    private lateinit var adapter: CommentAdapter

    private var isAdmin: Boolean = false

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
        viewModel = ViewModelProvider(this)[ComplaintDetailViewModel::class.java]
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
        isAdmin = intent.getBooleanExtra("isAdmin", false)

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

        val dialogComment = BottomSheetDialog(this)
        val dialogCommentBinding = DialogAddCommentBinding.inflate(layoutInflater)



        binding.btnAddComment.setOnClickListener {
            dialogComment.setContentView(dialogCommentBinding.root)

            dialogCommentBinding.btnCancel.setOnClickListener {
                dialogComment.dismiss()
            }

            dialogCommentBinding.btnSubmit.setOnClickListener {
                val commentText = dialogCommentBinding.etComment.text.toString()

                if (commentText.isEmpty()) {
                    ToastHelper.showToast(this, "Komentar tidak boleh kosong")
                    return@setOnClickListener
                }

                val comment = dialogCommentBinding.etComment.text.toString()
                viewModel.create(comment, uid!!, this)
                dialogComment.dismiss()
            }

            dialogComment.show()
        }

        viewModel.createState.observe(this){ state ->
            when (state) {
                is ResultState.Loading -> {
                    dialogComment.dismiss()
                }
                is ResultState.Success -> {
                    dialogComment.dismiss()
                    ToastHelper.showToast(this, "Berhasil")
                }
                is ResultState.Error -> {
                    dialogComment.dismiss()
                    ToastHelper.showToast(this, state.message)
                }
            }
        }


        val dialogStatus = BottomSheetDialog(this)
        val dialogStatusBinding = DialogChangeStatusBinding.inflate(layoutInflater)

        val statusList = listOf(
            "Proses",
            "Selesai",
            "Ditolak"
        )

        val statusAdapter = ArrayAdapter(
            this,
            R.layout.item_spinner_selected,
            statusList
        ).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }

        dialogStatusBinding.spinnerStatus.adapter = statusAdapter

        binding.btnChangeStatus.setOnClickListener {
            dialogStatus.setContentView(dialogStatusBinding.root)

            dialogStatusBinding.btnCancel.setOnClickListener {
                dialogStatus.dismiss()
            }

            dialogStatusBinding.btnSubmit.setOnClickListener {
                viewModel.updateStatus(
                    uid!!,
                    dialogStatusBinding.spinnerStatus.selectedItem.toString(),
                    dialogStatusBinding.etNote.text.toString()
                )
            }

            dialogStatus.show()
        }

        viewModel.updateState.observe(this){state ->
            when(state) {
                is ResultState.Loading -> {
                    dialogComment.dismiss()
                }
                is ResultState.Success -> {
                    dialogComment.dismiss()
                    ToastHelper.showToast(this, "Berhasil")
                    IntentHelper.finish(this)
                }
                is ResultState.Error -> {
                    dialogComment.dismiss()
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

        showView()
    }

    private fun showView() {
        if (isAdmin) {
            binding.btnAddComment.visibility = View.GONE
            binding.lineAddComment.visibility = View.GONE
            binding.btnChangeStatus.visibility = View.VISIBLE
        } else {
            binding.btnAddComment.visibility = View.VISIBLE
            binding.lineAddComment.visibility = View.VISIBLE
            binding.btnChangeStatus.visibility = View.GONE
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