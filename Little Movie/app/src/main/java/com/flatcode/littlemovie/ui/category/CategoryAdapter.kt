package com.flatcode.littlemovie.ui.category

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.databinding.ItemCategoryBinding
import com.flatcode.littlemovie.filter.CategoryFilter
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.GlideImage
import com.flatcode.littlemovie.utils.checkInterested
import com.flatcode.littlemovie.utils.isInterested
import com.flatcode.littlemovie.utils.openActivity
import java.text.MessageFormat

class CategoryAdapter(private val activity: Activity) :
    ListAdapter<Category, CategoryAdapter.ViewHolder>(DiffCallback), Filterable {

    private var fullList: List<Category> = emptyList()
    private var filter: CategoryFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val id = DATA.EMPTY + item.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val interestedCount = DATA.EMPTY + item.interestedCount
        val moviesCount = DATA.EMPTY + item.moviesCount

        holder.binding.image.GlideImage(false, image)

        if (item.name == DATA.EMPTY) {
            holder.binding.name.visibility = View.GONE
        } else {
            holder.binding.name.visibility = View.VISIBLE
            holder.binding.name.text = name
        }

        if (interestedCount == DATA.EMPTY) holder.binding.numberInterested.text = MessageFormat.format(
            "{0}{1}", DATA.EMPTY, DATA.ZERO
        ) else holder.binding.numberInterested.text = interestedCount

        if (moviesCount == DATA.EMPTY) holder.binding.numberMovies.text = MessageFormat.format(
            "{0}{1}", DATA.EMPTY, DATA.ZERO
        ) else holder.binding.numberMovies.text = moviesCount

        holder.binding.add.isInterested(id, DATA.CATEGORIES)
        holder.binding.add.setOnClickListener { holder.binding.add.checkInterested(DATA.CATEGORIES, id) }

        holder.binding.item.setOnClickListener {
            activity.openActivity<CategoryDetailsActivity>(
                DATA.CATEGORY_ID to id, DATA.CATEGORY_NAME to name
            )
        }
    }

    fun setFullList(list: List<Category>) {
        fullList = list
        submitList(list)
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = CategoryFilter(fullList, this)
        }
        return filter!!
    }

    object DiffCallback : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean =
            oldItem == newItem
    }

    inner class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)
}
