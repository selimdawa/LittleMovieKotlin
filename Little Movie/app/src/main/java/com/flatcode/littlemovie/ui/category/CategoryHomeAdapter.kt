package com.flatcode.littlemovie.ui.category

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.databinding.ItemCategoryHomeBinding
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.GlideImage
import com.flatcode.littlemovie.utils.openActivity

class CategoryHomeAdapter(private val context: Context?) :
    ListAdapter<Category, CategoryHomeAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryHomeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val id = item.id
        val name = item.name
        val image = item.image

        holder.binding.image.GlideImage(false, image)

        holder.binding.image.setOnClickListener {
            context?.openActivity<CategoryDetailsActivity>(
                DATA.CATEGORY_ID to id, DATA.CATEGORY_NAME to name
            )
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem == newItem
    }

    class ViewHolder(val binding: ItemCategoryHomeBinding) : RecyclerView.ViewHolder(binding.root)
}
