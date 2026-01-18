package com.example.lapordes.presentation.customer.complaint

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.UploadCallback
import com.example.lapordes.R
import com.example.lapordes.databinding.ActivityComplaintBinding
import com.example.lapordes.utils.IntentHelper
import java.io.File
import java.io.FileOutputStream

class ComplaintActivity : AppCompatActivity() {
    private var _binding: ActivityComplaintBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ComplaintViewModel

    var imageUri: String? = null
    var imageUrl: String? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityComplaintBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModel = ViewModelProvider(this)[ComplaintViewModel::class.java]

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        window.statusBarColor = getColor(R.color.secondary)

        binding.btnBack.setOnClickListener {
            IntentHelper.finish(this)
        }
        showDataSpinner()

        pickImageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK && result.data != null) {
                    val uri = result.data?.data
                    if (uri != null) {
                        contentResolver.takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        )

                        imageUri = uri.toString()
                        Glide.with(this)
                            .load(imageUri)
                            .into(binding.imgImage)

                        binding.emptyImageState.visibility = View.GONE
                        binding.imageContainer.visibility = View.VISIBLE
                    }
                }
            }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    openImagePicker()
                }
                else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                    IntentHelper.finish(this)
                }
            }

        binding.emptyImageState.setOnClickListener {
            openImagePicker()
        }
        binding.imageContainer.setOnClickListener {
            openImagePicker()
        }
    }

    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open input stream")

        val file = File(cacheDir, "upload_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)

        inputStream.copyTo(outputStream)

        inputStream.close()
        outputStream.close()

        return file
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }

    private fun showDataSpinner() {
        val kategoriList = listOf(
            "Infrastruktur",
            "Keamanan",
            "Kebersihan",
            "Bencana Alam"
        )

        val kategoriAdapter = ArrayAdapter(
            this,
            R.layout.item_spinner_selected,
            kategoriList
        ).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }

        binding.spinnerKategori.adapter = kategoriAdapter

        val priorityList = listOf(
            "Rendah",
            "Sedang",
            "Tinggi",
            "Darurat"
        )

        val priorityAdapter = ArrayAdapter(
            this,
            R.layout.item_spinner_selected,
            priorityList
        ).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }

        binding.spinnerPriority.adapter = priorityAdapter
    }

    private fun uploadImage(uriString: String) {
        val uri = Uri.parse(uriString)
        val file = uriToFile(uri)
        MediaManager.get().upload(file.absolutePath)
            .unsigned("outgamble")
            .callback(object : UploadCallback {

                override fun onStart(requestId: String) {}

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(
                    requestId: String,
                    resultData: Map<*, *>
                ) {
                    val url = resultData["secure_url"] as String
                    imageUrl = url
//                    val userId = UserIdPref(this@ReportsLocationActivity).get()
//                    viewModel.create(imageUrl!!, binding.etLocation.text.toString(), binding.etDate.text.toString(), binding.etDesc.text.toString(), userId)

                }

                override fun onError(
                    requestId: String,
                    error: com.cloudinary.android.callback.ErrorInfo
                ) {
                    error(error.description)
                }

                override fun onReschedule(requestId: String, error: com.cloudinary.android.callback.ErrorInfo) {}
            })
            .dispatch()
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