package com.flatcode.littlemovie.ui.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.GlideImage
import com.flatcode.littlemovie.utils.openActivity
import com.flatcode.littlemovie.databinding.ItemCategoryHomeBinding

class CategoryHomeAdapter(private val context: Context?, var list: ArrayList<Category?>) :
    RecyclerView.Adapter<CategoryHomeAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryHomeBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = item!!.id
        val name = item.name
        val image = item.image

        holder.binding.image.GlideImage(false, image)

        holder.binding.image.setOnClickListener {
            context?.openActivity<CategoryDetailsActivity>(
                DATA.CATEGORY_ID to id, DATA.CATEGORY_NAME to name
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class ViewHolder(val binding: ItemCategoryHomeBinding) : RecyclerView.ViewHolder(binding.root)
}
