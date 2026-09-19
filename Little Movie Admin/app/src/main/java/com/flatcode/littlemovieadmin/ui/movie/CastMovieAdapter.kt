package com.flatcode.littlemovieadmin.ui.movie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.model.Cast
import com.flatcode.littlemovieadmin.ui.cast.CastDetailsActivity
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.openActivity
import com.flatcode.littlemovieadmin.utils.loadGlideImage
import com.flatcode.littlemovieadmin.databinding.ItemCastMovieBinding

class CastMovieAdapter(private val activity: Activity, var list: ArrayList<Cast?>) :
    RecyclerView.Adapter<CastMovieAdapter.ViewHolder>() {
    private var binding: ItemCastMovieBinding? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemCastMovieBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val aboutMy = DATA.EMPTY + item.aboutMy

        holder.image.loadGlideImage(image, true)

        if (item.name == DATA.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = name
        }

        holder.item.setOnClickListener {
            activity.openActivity<CastDetailsActivity>(
                extras = arrayOf(
                    DATA.CAST_ID to id, DATA.CAST_NAME to name,
                    DATA.CAST_IMAGE to image, DATA.CAST_ABOUT to aboutMy
                )
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView
        var name: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            name = binding!!.name
            item = binding!!.item
        }
    }
}
