package com.flatcode.littlemovieadmin.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.databinding.ItemCategoryBinding
import com.flatcode.littlemovieadmin.model.Category
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.loadImage

class CategoryAdapter(
    private val onMoreClick: (Category) -> Unit, private val onItemClick: (Category) -> Unit
) : ListAdapter<Category, CategoryAdapter.ViewHolder>(DiffCallback()) {

    var list: List<Category> = emptyList()
        set(value) {
            field = value
            submitList(value)
        }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            list
        } else {
            list.filter { it.name?.contains(query, ignoreCase = true) == true }
        }
        submitList(filteredList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onMoreClick, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        val binding: ItemCategoryBinding,
        private val onMoreClick: (Category) -> Unit,
        private val onItemClick: (Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Category) {
            val name = item.name ?: DATA.EMPTY
            val image = item.image ?: DATA.EMPTY
            val interestedCount = item.interestedCount
            val moviesCount = item.moviesCount

            binding.image.loadImage(image, isUser = false)

            if (name == DATA.EMPTY) {
                binding.name.visibility = View.GONE
            } else {
                binding.name.visibility = View.VISIBLE
                binding.name.text = name
            }

            binding.numberInterested.text = interestedCount.toString()
            binding.numberMovies.text = moviesCount.toString()

            binding.more.setOnClickListener { onMoreClick(item) }
            binding.item.setOnClickListener { onItemClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem == newItem
    }
}
