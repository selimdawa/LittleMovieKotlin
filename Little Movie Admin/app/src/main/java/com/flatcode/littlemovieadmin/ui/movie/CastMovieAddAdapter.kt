package com.flatcode.littlemovieadmin.ui.movie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.databinding.ItemCastMovieAddBinding
import com.flatcode.littlemovieadmin.model.Cast
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.DATA.castMovie
import com.flatcode.littlemovieadmin.utils.loadGlideImage

class CastMovieAddAdapter(private val activity: Activity) :
    ListAdapter<Cast, CastMovieAddAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCastMovieAddBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val id = DATA.EMPTY + item.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image

        holder.binding.image.loadGlideImage(image, true)

        if (name == DATA.EMPTY) {
            holder.binding.name.visibility = View.GONE
        } else {
            holder.binding.name.visibility = View.VISIBLE
            holder.binding.name.text = name
        }

        castAddRemove = castMovie
        checkRemove(id, holder.binding.add, holder.binding.remove)
        checkAdd(id, holder.binding.add, holder.binding.remove)

        holder.binding.add.setOnClickListener {
            castAddRemove = castAddRemove as ArrayList<String?> + id
            checkRemove(id, holder.binding.add, holder.binding.remove)
            checkAdd(id, holder.binding.add, holder.binding.remove)
        }

        holder.binding.remove.setOnClickListener {
            castAddRemove = castAddRemove as ArrayList<String?> - id
            checkRemove(id, holder.binding.add, holder.binding.remove)
            checkAdd(id, holder.binding.add, holder.binding.remove)
        }
    }

    class ViewHolder(val binding: ItemCastMovieAddBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean = oldItem == newItem
    }

    private fun checkAdd(id: String, add: ImageView, remove: ImageView) {
        for (i in castAddRemove!!.indices) {
            if (castAddRemove!![i] == id) {
                add.visibility = View.GONE
                remove.visibility = View.VISIBLE
            }
        }
    }

    private fun checkRemove(id: String, add: ImageView, remove: ImageView) {
        for (i in castAddRemove!!.indices) {
            if (castAddRemove!![i] != id) {
                add.visibility = View.VISIBLE
                remove.visibility = View.GONE
            }
        }
    }

    companion object {
        var castAddRemove: List<String?>? = null
    }
}