package com.flatcode.littlemovie.ui.cast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.databinding.ItemCastMovieBinding
import com.flatcode.littlemovie.model.Cast
import com.flatcode.littlemovie.utils.loadImage

class CastMovieAdapter(private val onItemClick: (Cast) -> Unit) :
    ListAdapter<Cast, CastMovieAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCastMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    object DiffCallback : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean = oldItem == newItem
    }

    class ViewHolder(private val binding: ItemCastMovieBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cast, onItemClick: (Cast) -> Unit) {
            val name = item.name ?: ""
            val image = item.image ?: ""

            with(binding) {
                this.image.loadImage(true, image)

                if (name.isEmpty()) {
                    this.name.visibility = View.GONE
                } else {
                    this.name.visibility = View.VISIBLE
                    this.name.text = name
                }

                this.item.setOnClickListener { onItemClick(item) }
            }
        }
    }
}
