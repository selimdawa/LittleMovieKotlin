package com.flatcode.littlemovieadmin.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ItemMainBinding
import com.flatcode.littlemovieadmin.model.Main

class MainAdapter(
    private val onItemClick: (Main) -> Unit
) : ListAdapter<Main, MainAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        val binding: ItemMainBinding,
        private val onItemClick: (Main) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: Main) {
            val image = model.image
            val number = model.number
            val name = model.title

            if (image != 0) {
                binding.image.setImageResource(image)
            } else {
                binding.image.setImageResource(R.drawable.ic_load)
            }

            if (number != 0) {
                binding.number.visibility = View.VISIBLE
                binding.number.text = number.toString()
            } else {
                binding.number.visibility = View.GONE
            }

            binding.name.text = name

            itemView.setOnClickListener { onItemClick(model) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Main>() {
        override fun areItemsTheSame(oldItem: Main, newItem: Main): Boolean =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: Main, newItem: Main): Boolean =
            oldItem.title == newItem.title && oldItem.image == newItem.image && 
            oldItem.number == newItem.number
    }
}
