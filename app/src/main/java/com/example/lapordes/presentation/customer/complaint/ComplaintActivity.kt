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
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.databinding.ActivityComplaintBinding
import com.example.lapordes.utils.IntentHelper
import com.example.lapordes.utils.ToastHelper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileOutputStream

class ComplaintActivity : AppCompatActivity() {
    private var _binding: ActivityComplaintBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ComplaintViewModel

    private var imageUri: String? = null
    private var imageUrl: String? = null
    private var myLat: Double? = null
    private var myLng: Double? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var fusedLocationClient: FusedLocationProviderClient


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
                    getCurrentLocation()
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

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getCurrentLocation()

        viewModel.createState.observe(this){state ->
            when(state) {
                is ResultState.Loading -> {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.btnSend.visibility = View.GONE
                    binding.btnBack.isEnabled = false
                }
                is ResultState.Success -> {
                    ToastHelper.showToast(this, "Berhasil membuat pengaduan!")
                    IntentHelper.finish(this)
                }
                is ResultState.Error -> {
                    binding.pbLoading.visibility = View.GONE
                    binding.btnSend.visibility = View.VISIBLE
                    binding.btnBack.isEnabled = true

                    ToastHelper.showToast(this, state.message)
                }
            }
        }
    }

    private fun getCurrentLocation() {
        if (
            checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    myLat = location.latitude
                    myLng = location.longitude
                } else {
                    Toast.makeText(this, "Lokasi tidak tersedia", Toast.LENGTH_SHORT).show()
                }
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

                override fun onStart(requestId: String) {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.btnSend.visibility = View.GONE
                    binding.btnBack.isEnabled = false
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.btnSend.visibility = View.GONE
                    binding.btnBack.isEnabled = false
                }

                override fun onSuccess(
                    requestId: String,
                    resultData: Map<*, *>
                ) {
                    val url = resultData["secure_url"] as String
                    imageUrl = url
//
                    val title = binding.etTitle.text.toString()
                    val category = binding.spinnerKategori.selectedItem.toString()
                    val priority = binding.spinnerPriority.selectedItem.toString()
                    val desc = binding.etDesc.text.toString()
                    val lat = myLat
                    val lng = myLng

                    viewModel.create(title, category, priority, desc, url, lat!!, lng!!, this@ComplaintActivity)
                }

                override fun onError(
                    requestId: String,
                    error: com.cloudinary.android.callback.ErrorInfo
                ) {
                    error(error.description)
                    binding.pbLoading.visibility = View.GONE
                    binding.btnSend.visibility = View.VISIBLE
                    binding.btnBack.isEnabled = true
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