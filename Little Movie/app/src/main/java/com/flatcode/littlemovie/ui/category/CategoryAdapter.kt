package com.flatcode.littlemovie.ui.category

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.filter.CategoryFilter
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.VOID
import com.flatcode.littlemovie.utils.VOID.GlideImage
import com.flatcode.littlemovie.databinding.ItemCategoryBinding
import java.text.MessageFormat

class CategoryAdapter(private val activity: Activity, var list: ArrayList<Category?>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>(), Filterable {

    var filterList: ArrayList<Category?>
    private var filter: CategoryFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val interestedCount = DATA.EMPTY + item.interestedCount
        val moviesCount = DATA.EMPTY + item.moviesCount

        GlideImage(false, activity, image, holder.binding.image)

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

        VOID.isInterested(holder.binding.add, id, DATA.CATEGORIES)
        holder.binding.add.setOnClickListener { VOID.checkInterested(holder.binding.add, DATA.CATEGORIES, id) }

        holder.binding.item.setOnClickListener {
            VOID.IntentExtra2(
                activity, CategoryDetailsActivity::class.java, DATA.CATEGORY_ID, id, DATA.CATEGORY_NAME, name
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = CategoryFilter(filterList, this)
        }
        return filter!!
    }

    inner class ViewHolder(val binding: ItemCategoryBinding) : RecyclerView.ViewHolder(binding.root)

    init {
        filterList = list
    }
}
