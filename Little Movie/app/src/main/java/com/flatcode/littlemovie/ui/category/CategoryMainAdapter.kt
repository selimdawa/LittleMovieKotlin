package com.flatcode.littlemovie.ui.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.databinding.ItemCategoryMainBinding
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.utils.*

class CategoryMainAdapter(private val onItemClick: (Category) -> Unit) :
    ListAdapter<Category, CategoryMainAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryMainBinding.inflate(LayoutInflater.from(parent.context), parent, false)
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

    class ViewHolder(private val binding: ItemCategoryMainBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category, onItemClick: (Category) -> Unit) {
            val name = item.name ?: ""
            val image = item.image ?: ""

            with(binding) {
                this.image.loadImage(false, image)
                imageBlur.loadImageBlur(false, image, 50)

                if (name.isEmpty()) {
                    this.name.visibility = View.GONE
                } else {
                    this.name.visibility = View.VISIBLE
                    this.name.text = name
                }

                card.setOnClickListener { onItemClick(item) }
            }
        }
    }
}
