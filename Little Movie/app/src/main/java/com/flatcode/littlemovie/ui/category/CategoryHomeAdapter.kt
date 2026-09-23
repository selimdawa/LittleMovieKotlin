package com.flatcode.littlemovie.ui.category

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.databinding.ItemCategoryHomeBinding
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.utils.loadImage

class CategoryHomeAdapter(private val onItemClick: (Category) -> Unit) :
    ListAdapter<Category, CategoryHomeAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCategoryHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    object DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem == newItem
    }

    class ViewHolder(private val binding: ItemCategoryHomeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category, onItemClick: (Category) -> Unit) {
            val image = item.image ?: ""

            with(binding) {
                this.image.loadImage(false, image)
                this.image.setOnClickListener { onItemClick(item) }
            }
        }
    }
}