package com.flatcode.littlemovieadmin.ui.category

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
import com.flatcode.littlemovieadmin.filter.CategoryFilter
import com.flatcode.littlemovieadmin.model.Category
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.intentExtra2
import com.flatcode.littlemovieadmin.utils.loadGlideImage
import com.flatcode.littlemovieadmin.utils.moreDeleteCategory
import com.flatcode.littlemovieadmin.databinding.ItemCategoryBinding
import java.text.MessageFormat

class CategoryAdapter(private val activity: Activity, var list: ArrayList<Category?>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>(), Filterable {

    private var binding: ItemCategoryBinding? = null
    var filterList: ArrayList<Category?>
    private var filter: CategoryFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemCategoryBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val interestedCount = DATA.EMPTY + item.interestedCount
        val moviesCount = DATA.EMPTY + item.moviesCount

        holder.image.loadGlideImage(image, false)

        if (item.name == DATA.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = name
        }

        if (interestedCount == DATA.EMPTY) holder.numberInterested.text = MessageFormat.format(
            "{0}{1}", DATA.EMPTY, DATA.ZERO
        ) else holder.numberInterested.text = interestedCount

        if (moviesCount == DATA.EMPTY) holder.numberMovies.text = MessageFormat.format(
            "{0}{1}", DATA.EMPTY, DATA.ZERO
        ) else holder.numberMovies.text = moviesCount

        holder.more.setOnClickListener {
            activity.moreDeleteCategory(item, DATA.NULL, DATA.NULL, DATA.NULL, false, false)
        }

        holder.item.setOnClickListener {
            activity.intentExtra2(
                CategoryDetailsActivity::class.java, DATA.CATEGORY_ID, id, DATA.CATEGORY_NAME, name
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

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView
        var more: ImageView
        var name: TextView
        var numberMovies: TextView
        var numberInterested: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            name = binding!!.name
            more = binding!!.more
            numberMovies = binding!!.numberMovies
            numberInterested = binding!!.numberInterested
            item = binding!!.item
        }
    }

    init {
        filterList = list
    }
}
