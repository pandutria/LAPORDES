package com.example.lapordes.presentation.admin

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import com.example.lapordes.R
import com.example.lapordes.data.model.Complaint
import com.example.lapordes.data.state.ResultState
import com.example.lapordes.databinding.ActivityAdminMainBinding
import com.example.lapordes.databinding.FragmentHomeBinding
import com.example.lapordes.presentation.adapter.ComplaintAdapter
import com.example.lapordes.presentation.complaint.ComplaintDetailActivity
import com.example.lapordes.presentation.customer.home.HomeViewModel
import com.example.lapordes.utils.IntentHelper
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter

class AdminMainActivity : AppCompatActivity() {
    private var _binding: ActivityAdminMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: AdminMainViewModel
    private lateinit var adapter: ComplaintAdapter

    private var list = listOf<Complaint>()
    private var selectedCategory = "Semua"
    private var selectedPriority = "Semua"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityAdminMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true
        viewModel = ViewModelProvider(this)[AdminMainViewModel::class.java]


        showDataSpinner()

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
                putBoolean("isAdmin", true)
            }
            IntentHelper.navigate(this, ComplaintDetailActivity::class.java, bundle)
        }

        viewModel.get(this)
        viewModel.getState.observe(this){state ->
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
                    list = state.data
                    filterData()
                    binding.rvPengaduan.adapter = adapter

                    binding.tvTotalProses.text = state.data.count{x -> x.status == "Proses"}.toString()
                    binding.tvTotalBerhasil.text = state.data.count{x -> x.status == "Selesai"}.toString()
                    binding.tvTotalDitolak.text = state.data.count{x -> x.status == "Ditolak"}.toString()
                    binding.tvTotalPengaduan.text = state.data.count().toString()

                    updatePieChart(state.data.count{x -> x.status == "Proses"}, state.data.count{x -> x.status == "Selesai"}, state.data.count{x -> x.status == "DiTolak"})

                    binding.pbLoading.visibility = View.GONE
                    binding.rvPengaduan.visibility = View.VISIBLE

                    if (state.data.isEmpty()) {
                        binding.rvPengaduan.visibility = View.GONE
                    }
                }
                is ResultState.Error -> {
                    viewModel.get(this)
                }
            }
        }
    }

    private fun updatePieChart(proses: Int, berhasil: Int, ditolak: Int) {
        val entries = ArrayList<PieEntry>()

        if (proses > 0) entries.add(PieEntry(proses.toFloat(), "Proses"))
        if (berhasil > 0) entries.add(PieEntry(berhasil.toFloat(), "Selesai"))
        if (ditolak > 0) entries.add(PieEntry(ditolak.toFloat(), "Ditolak"))

        if (entries.isEmpty()) {
            entries.add(PieEntry(1f, "Tidak ada data"))
        }

        val dataSet = PieDataSet(entries, "").apply {
            sliceSpace = 3f
            selectionShift = 5f

            colors = if (entries.size == 1 && entries[0].label == "Tidak ada data") {
                listOf(Color.LTGRAY)
            } else {
                listOf(
                    Color.parseColor("#F57C00"),
                    Color.parseColor("#388E3C"),
                    Color.parseColor("#D32F2F")
                ).take(entries.size)
            }

            valueTextSize = 12f
            valueTextColor = Color.WHITE
        }

        val data = PieData(dataSet).apply {
            setValueFormatter(PercentFormatter(binding.pieChart))
            setValueTextSize(11f)
            setValueTextColor(Color.WHITE)
        }

        binding.pieChart.data = data
        binding.pieChart.invalidate()
        binding.pieChart.animateY(1000, Easing.EaseInOutQuad)
    }

    private fun showDataSpinner() {
        val kategoriList = listOf(
            "Semua",
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

        binding.spinnerKategori.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCategory = kategoriList[position]
                filterData()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedCategory = "Semua"
                filterData()
            }
        }

        val priorityList = listOf(
            "Semua",
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

        binding.spinnerStatus.adapter = priorityAdapter

        binding.spinnerStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedPriority = priorityList[position]
                filterData()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                selectedPriority = "Semua"
                filterData()
            }
        }
    }

    private fun filterData() {
        var filtered = list

        if (selectedCategory != "Semua") {
            filtered = filtered.filter { it.category == selectedCategory }
        }

        if (selectedPriority != "Semua") {
            filtered = filtered.filter { it.priority == selectedPriority }
        }

        adapter.setData(filtered)

        binding.rvPengaduan.visibility = if (filtered.isEmpty()) View.GONE else View.VISIBLE
    }
}