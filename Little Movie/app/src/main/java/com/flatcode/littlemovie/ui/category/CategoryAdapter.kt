package com.flatcode.littlemovie.ui.category

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.databinding.ItemCategoryBinding
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.checkInterested
import com.flatcode.littlemovie.utils.isInterested
import com.flatcode.littlemovie.utils.loadImage
import java.util.Locale

class CategoryAdapter(private val onItemClick: (Category) -> Unit) :
    ListAdapter<Category, CategoryAdapter.ViewHolder>(DiffCallback) {

    private var fullList: List<Category> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    fun setFullList(list: List<Category>) {
        fullList = list
        submitList(list)
    }

    fun filter(query: String?) {
        if (query.isNullOrEmpty()) {
            submitList(fullList)
        } else {
            val q = query.uppercase(Locale.getDefault())
            val filtered = fullList.filter {
                it.name?.uppercase(Locale.getDefault())?.contains(q) == true
            }
            submitList(filtered)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem == newItem
    }

    class ViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Category, onItemClick: (Category) -> Unit) {
            val id = item.id
            val name = item.name ?: ""
            val image = item.image ?: ""
            val interestedCount = item.interestedCount.toString()
            val moviesCount = item.moviesCount.toString()

            with(binding) {
                this.image.loadImage(false, image)

                if (name.isEmpty()) {
                    this.name.visibility = View.GONE
                } else {
                    this.name.visibility = View.VISIBLE
                    this.name.text = name
                }

                numberInterested.text = interestedCount
                numberMovies.text = moviesCount

                add.isInterested(id, DATA.CATEGORIES)
                add.setOnClickListener { add.checkInterested(DATA.CATEGORIES, id) }

                this.item.setOnClickListener { onItemClick(item) }
            }
        }
    }
}
