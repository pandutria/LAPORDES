package com.example.lapordes.presentation.customer.profile.update

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
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
import com.example.lapordes.data.local.UserPref
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.databinding.ActivityProfileUpdateBinding
import com.example.lapordes.utils.IntentHelper
import com.example.lapordes.utils.ToastHelper
import com.google.firebase.Timestamp
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ProfileUpdateActivity : AppCompatActivity() {
    private var _binding: ActivityProfileUpdateBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: ProfileUpdateViewModel

    private var imageUri: String? = null
    private var imageUrl: String? = null
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityProfileUpdateBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        window.statusBarColor = getColor(R.color.secondary)
        viewModel = ViewModelProvider(this)[ProfileUpdateViewModel::class.java]

        binding.btnBack.setOnClickListener {
            IntentHelper.finish(this)
        }

        showCbo()

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
                            .placeholder(R.drawable.empty_profile)
                            .into(binding.imgProfile)
                    }
                }
            }

        binding.imgProfile.setOnClickListener {
            openImagePicker()
        }

        binding.etBirthDate.setOnClickListener {
            showDatePicker()
        }

        viewModel.me(this)
        viewModel.meState.observe(this) { state ->
            when (state) {
                is ResultState.Loading -> {

                }

                is ResultState.Success -> {
                    binding.etEmail.setText(state.data.email)
                    binding.etUsername.setText(state.data.fullname)
                    binding.etNIK.setText(state.data.nik)
                    binding.etPhone.setText(state.data.phone)
                    binding.etFullName.setText(state.data.fullname)
                    binding.etPassword.setText(state.data.password)

                    if (state.data.birth != null) {
                        val timestamp = state.data.birth
                        val date = timestamp.toDate()

                        val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                        binding.etBirthDate.setText(formatter.format(date))
                    }

                    Glide.with(this)
                        .load(state.data.image)
                        .placeholder(R.drawable.empty_profile)
                        .error(R.drawable.empty_profile)
                        .into(binding.imgProfile)


                    val gender = state.data.gender
                    val adapter = binding.spinnerGender.adapter as ArrayAdapter<String>

                    gender?.let {
                        val index = adapter.getPosition(it)
                        if (index >= 0) {
                            binding.spinnerGender.setSelection(index)
                        }
                    }
                }

                is ResultState.Error -> {

                }
            }
        }

        binding.btnUpdate.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val fullname = binding.etFullName.text.toString().trim()
            val username = binding.etUsername.text.toString().trim()
            val phone = binding.etPhone.text.toString().trim()
            val nik = binding.etNIK.text.toString().trim()
            val gender = binding.spinnerGender.selectedItem.toString()

            val birthText = binding.etBirthDate.text.toString()
            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val birthDate = formatter.parse(birthText)
            val birthTimestamp = Timestamp(birthDate!!)

            if (email.isEmpty() || password.isEmpty())
                return@setOnClickListener ToastHelper.showToast(this, "Semua input wajib di isi")

            if (imageUri != null) {
                uploadProfileImage(imageUri!!) { url ->
                    submitUpdate(
                        email,
                        password,
                        fullname,
                        username,
                        phone,
                        nik,
                        gender,
                        birthTimestamp,
                        url
                    )
                }
            } else {
                submitUpdate(
                    email,
                    password,
                    fullname,
                    username,
                    phone,
                    nik,
                    gender,
                    birthTimestamp,
                    imageUrl ?: ""
                )
            }
        }


        viewModel.updateState.observe(this) { state ->
            when (state) {
                is ResultState.Loading -> {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.btnUpdate.visibility = View.GONE
                }

                is ResultState.Success -> {
                    val user = UserPref(this).save(state.data)
                    ToastHelper.showToast(this, "Berhasil edit data")
                    IntentHelper.finish(this)
                }

                is ResultState.Error -> {
                    ToastHelper.showToast(this, state.message)
                }
            }
        }
    }

    private fun submitUpdate(
        email: String,
        password: String,
        fullname: String,
        username: String,
        phone: String,
        nik: String,
        gender: String,
        birth: Timestamp,
        image: String
    ) {
        viewModel.update(
            this,
            email,
            password,
            image,
            fullname,
            username,
            phone,
            nik,
            gender,
            birth
        )
    }


    private fun uploadProfileImage(
        uriString: String,
        onSuccess: (String) -> Unit
    ) {
        val uri = Uri.parse(uriString)
        val file = uriToFile(uri)

        MediaManager.get().upload(file.absolutePath)
            .unsigned("outgamble")
            .callback(object : UploadCallback {

                override fun onStart(requestId: String) {
                    binding.pbLoading.visibility = View.VISIBLE
                    binding.btnUpdate.visibility = View.GONE
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as String
                    onSuccess(url)
                }

                override fun onError(
                    requestId: String,
                    error: com.cloudinary.android.callback.ErrorInfo
                ) {
                    binding.pbLoading.visibility = View.GONE
                    binding.btnUpdate.visibility = View.VISIBLE
                    ToastHelper.showToast(this@ProfileUpdateActivity, error.description)
                }

                override fun onReschedule(
                    requestId: String,
                    error: com.cloudinary.android.callback.ErrorInfo
                ) {
                }
            })
            .dispatch()
    }


    private fun uriToFile(uri: Uri): File {
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IllegalArgumentException("Cannot open input stream")

        val file = File(cacheDir, "profile_${System.currentTimeMillis()}.jpg")
        val outputStream = FileOutputStream(file)

        inputStream.copyTo(outputStream)

        inputStream.close()
        outputStream.close()

        return file
    }

    @SuppressLint("DefaultLocale")
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->

                val formattedDate = String.format(
                    "%02d-%02d-%04d",
                    selectedDay,
                    selectedMonth + 1,
                    selectedYear
                )

                binding.etBirthDate.setText(formattedDate)
            },
            year,
            month,
            day
        )

        datePicker.show()
    }


    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        pickImageLauncher.launch(intent)
    }


    private fun showCbo() {
        val genderList = listOf(
            "Laki Laki",
            "Perempuan",
        )

        val priorityAdapter = ArrayAdapter(
            this,
            R.layout.item_spinner_selected,
            genderList
        ).apply {
            setDropDownViewResource(R.layout.item_spinner_dropdown)
        }

        binding.spinnerGender.adapter = priorityAdapter
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