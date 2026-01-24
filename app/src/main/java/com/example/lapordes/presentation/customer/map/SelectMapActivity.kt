package com.example.lapordes.presentation.customer.map

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lapordes.R
import com.example.lapordes.databinding.ActivitySelectMapBinding
import com.example.lapordes.utils.IntentHelper
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

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
        setContentView(R.layout.activity_select_map)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnBack.setOnClickListener {
            IntentHelper.finish(this)
        }

        lat = intent.getDoubleExtra("lat", 0.0)
        lng = intent.getDoubleExtra("lng", 0.0)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (lat != 0.0 && lng != 0.0) {
            val initialLocation = LatLng(lat!!, lng!!)
            mMap.addMarker(MarkerOptions().position(initialLocation).title("Lokasi Terpilih"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 13f))
            selectedLatLng = initialLocation
        } else {
            val blitarCenter = LatLng(-8.0833, 112.1500)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(blitarCenter, 13f))
        }

        mMap.setOnMapClickListener { latLng ->
            mMap.clear()
            mMap.addMarker(MarkerOptions().position(latLng).title("Lokasi Terpilih"))
            selectedLatLng = latLng
        }
    }
}