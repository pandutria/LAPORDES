package com.example.lapordes.presentation.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.example.lapordes.R
import com.example.lapordes.data.local.UserPref
import com.example.lapordes.data.model.Complaint
import com.example.lapordes.databinding.ItemComplaintBinding
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

class ComplaintAdapter(
    private val list: MutableList<Complaint> = mutableListOf(),
    private val onClick: (Complaint) -> Unit
): RecyclerView.Adapter<ComplaintAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemComplaintBinding): RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("DefaultLocale", "NewApi")
        fun bind(complaint: Complaint, onClick: (Complaint) -> Unit) {
            binding.tvTitle.text = complaint.title
            binding.tvDesc.text = complaint.description
            binding.tvStatus.text = complaint.status
            binding.tvPriority.text = complaint.priority
            binding.tvCategory.text = complaint.category
            binding.tvUsername.text = "masyarakat"
            binding.tvId.text = "ID: #${complaint.uid.take(15)}"

            val user = UserPref(binding.root.context).get()
            if (complaint.user.email == user!!.email) {
                binding.tvFullname.text = "Anda"
            } else {
                binding.tvFullname.text = complaint.user.email
            }

            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale("id", "ID"))

            complaint.created_at?.let { ts ->
                val date = ts.toDate().toInstant()
                    .atZone(ZoneId.systemDefault())

                binding.tvCreateAt.text = "Dibuat: ${date.format(formatter)}"

                val days = ChronoUnit.DAYS.between(date, ZonedDateTime.now())
                binding.tvDay.text = "($days Hari)"
            }

            complaint.updated_at?.let { ts ->
                val date = ts.toDate().toInstant()
                    .atZone(ZoneId.systemDefault())

                binding.tvUpdateAt.text = "Diperbarui: ${date.format(formatter)}"
            }

            if (complaint.status == "Proses") binding.tvStatus.setBackgroundResource(R.drawable.bg_status_process)
            if (complaint.status == "Selesai") binding.tvStatus.setBackgroundResource(R.drawable.bg_status_approved)
            if (complaint.status == "Ditolak") binding.tvStatus.setBackgroundResource(R.drawable.bg_status_rejected)

            binding.root.setOnClickListener {
                onClick(complaint)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemComplaintBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], onClick)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(data: List<Complaint>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }
}