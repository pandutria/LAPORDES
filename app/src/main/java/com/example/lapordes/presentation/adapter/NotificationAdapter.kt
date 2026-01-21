package com.example.lapordes.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lapordes.R
import com.example.lapordes.data.model.Complaint
import com.example.lapordes.data.model.Notification
import com.example.lapordes.databinding.ItemComplaintBinding
import com.example.lapordes.databinding.ItemMessageBinding
import com.example.lapordes.presentation.adapter.ComplaintAdapter.ViewHolder
import com.example.lapordes.utils.TimeAgoHelper

class NotificationAdapter(
    private val list: MutableList<Notification> = mutableListOf()
): RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemMessageBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(notification: Notification) {
            binding.tvStatusBadge.text = notification.complaint.status
            binding.tvComplaintTitle.text = notification.complaint.title
            binding.tvMessageTime.text = TimeAgoHelper.getTimeAgo(notification.created_at)
            binding.tvComplaintNumber.text = notification.complaint.uid.take(15)
            binding.tvMessageContent.text = notification.complaint.description

            if (notification.complaint.status == "Proses") binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_process)
            if (notification.complaint.status == "Selesai") binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_approved)
            if (notification.complaint.status == "Ditolak") binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_status_rejected)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationAdapter.ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(data: List<Notification>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }
}