package com.flatcode.littlemovie.ui.category

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.GlideBlur
import com.flatcode.littlemovie.utils.GlideImage
import com.flatcode.littlemovie.utils.openActivity
import com.flatcode.littlemovie.databinding.ItemCategoryMainBinding

class CategoryMainAdapter(private val context: Context?, var list: ArrayList<Category?>) :
    RecyclerView.Adapter<CategoryMainAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCategoryMainBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image

        holder.binding.image.GlideImage(false, image)
        holder.binding.imageBlur.GlideBlur(false, image, 50)

        if (name == DATA.EMPTY) {
            holder.binding.name.visibility = View.GONE
        } else {
            holder.binding.name.visibility = View.VISIBLE
            holder.binding.name.text = name
        }

        holder.binding.card.setOnClickListener {
            context?.openActivity<CategoryDetailsActivity>(
                DATA.CATEGORY_ID to id, DATA.CATEGORY_NAME to name
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemCategoryMainBinding) : RecyclerView.ViewHolder(binding.root)
}
