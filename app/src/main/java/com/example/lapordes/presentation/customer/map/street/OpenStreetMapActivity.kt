package com.example.lapordes.presentation.customer.map.street

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lapordes.R
import com.example.lapordes.databinding.ActivityOpenStreetMapBinding
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback
import com.google.android.gms.maps.StreetViewPanorama
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment
import com.google.android.gms.maps.model.LatLng

class OpenStreetMapActivity : AppCompatActivity(), OnStreetViewPanoramaReadyCallback {
    private var _binding: ActivityOpenStreetMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var streetViewPanorama: StreetViewPanorama

    private lateinit var locationLatLng: LatLng

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityOpenStreetMapBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val lat = intent.getDoubleExtra("lat", 0.0)
        val lng = intent.getDoubleExtra("lng", 0.0)

        val streetViewFragment =
            supportFragmentManager.findFragmentById(R.id.streetViewFragment)
                    as SupportStreetViewPanoramaFragment

        streetViewFragment.getStreetViewPanoramaAsync(this)

        locationLatLng = LatLng(lat, lng)
    }

    override fun onStreetViewPanoramaReady(panorama: StreetViewPanorama) {
        streetViewPanorama = panorama
        streetViewPanorama.setPosition(locationLatLng)
    }
}