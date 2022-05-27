package com.corneliudascalu.challenge

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.corneliudascalu.challenge.databinding.ItemPhotoBinding

class PhotoAdapter : ListAdapter<String, PhotoViewHolder>(DiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        return PhotoViewHolder(parent)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    object DiffUtil : androidx.recyclerview.widget.DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem == newItem
        }

    }
}

class PhotoViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)) {
    val binding = ItemPhotoBinding.bind(itemView)
    fun bind(url: String) {
        binding.photoUrl.text = url
    }
}