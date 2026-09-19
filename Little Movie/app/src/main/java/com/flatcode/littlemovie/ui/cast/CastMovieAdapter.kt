package com.flatcode.littlemovie.ui.cast

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.model.Cast
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.VOID
import com.flatcode.littlemovie.databinding.ItemCastMovieBinding

class CastMovieAdapter(private val activity: Activity, var list: ArrayList<Cast?>) :
    RecyclerView.Adapter<CastMovieAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCastMovieBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val aboutMy = DATA.EMPTY + item.aboutMy

        VOID.GlideImage(true, activity, image, holder.binding.image)

        if (item.name == DATA.EMPTY) {
            holder.binding.name.visibility = View.GONE
        } else {
            holder.binding.name.visibility = View.VISIBLE
            holder.binding.name.text = name
        }

        holder.binding.item.setOnClickListener {
            VOID.IntentExtra4(
                activity, CastDetailsActivity::class.java, DATA.CAST_ID, id, DATA.CAST_NAME,
                name, DATA.CAST_IMAGE, image, DATA.CAST_ABOUT, aboutMy
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemCastMovieBinding) : RecyclerView.ViewHolder(binding.root)
}
