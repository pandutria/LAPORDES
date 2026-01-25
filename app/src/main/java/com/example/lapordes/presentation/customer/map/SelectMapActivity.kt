package com.example.lapordes.presentation.customer.map

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.lapordes.R
import com.example.lapordes.databinding.ActivitySelectMapBinding
import com.example.lapordes.presentation.customer.map.street.OpenStreetMapActivity
import com.example.lapordes.utils.IntentHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.jvm.java

class SelectMapActivity : AppCompatActivity(), OnMapReadyCallback {
    private var _binding: ActivitySelectMapBinding? = null
    private val binding get() = _binding!!
    private lateinit var mMap: GoogleMap
    private var selectedLatLng: LatLng? = null
    private var lat: Double? = null
    private var lng: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivitySelectMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = false
        window.statusBarColor = ContextCompat.getColor(this, R.color.secondary)

        binding.btnBack.setOnClickListener {
            IntentHelper.finish(this)
        }

        lat = intent.getDoubleExtra("lat", 0.0)
        lng = intent.getDoubleExtra("lng", 0.0)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnSend.setOnClickListener {
            if (selectedLatLng == null) {
                Toast.makeText(this, "Silakan pilih lokasi terlebih dahulu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val resultIntent = Intent().apply {
                putExtra("lat", selectedLatLng!!.latitude)
                putExtra("lng", selectedLatLng!!.longitude)
            }

            setResult(RESULT_OK, resultIntent)
            IntentHelper.finish(this)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val initialLocation = LatLng(lat!!, lng!!)
        mMap.addMarker(MarkerOptions().position(initialLocation).title("Lokasi Terpilih"))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 16f))
        selectedLatLng = initialLocation


        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Lokasi Terpilih"))
            selectedLatLng = latLng
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