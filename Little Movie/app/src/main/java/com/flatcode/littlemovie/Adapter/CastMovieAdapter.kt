package com.flatcode.littlemovie.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.Model.Cast
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.databinding.ItemCastMovieBinding

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

        VOID.GlideImage(true, activity, image, holder.image)

        if (item.name == DATA.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = name
        }

        holder.item.setOnClickListener {
            VOID.IntentExtra4(
                activity, CLASS.CAST_DETAILS, DATA.CAST_ID, id, DATA.CAST_NAME,
                name, DATA.CAST_IMAGE, image, DATA.CAST_ABOUT, aboutMy
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