package com.example.lapordes.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.lapordes.data.local.UserPref
import com.example.lapordes.data.model.Comment
import com.example.lapordes.databinding.ItemCommentBinding

class CommentAdapter(
    private val list: MutableList<Comment> = mutableListOf(),
): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {
    inner class ViewHolder(val binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.tvComment.text = comment.comment
            binding.tvRole.text = "Masyarakat"

            val user = UserPref(binding.root.context).get()
            if (comment.user.email == user!!.email) {
                binding.tvName.text = "Anda"
            } else {
                binding.tvName.text = comment.user.email
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentAdapter.ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentAdapter.ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setData(data: List<Comment>) {
        list.clear()
        list.addAll(data)
        notifyDataSetChanged()
    }
}