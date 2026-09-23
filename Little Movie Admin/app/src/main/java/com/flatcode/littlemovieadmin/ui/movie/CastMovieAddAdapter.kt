package com.flatcode.littlemovieadmin.ui.movie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.databinding.ItemCastMovieAddBinding
import com.flatcode.littlemovieadmin.model.Cast
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.loadImage

class CastMovieAddAdapter(
    initialSelectedIds: List<String?>
) : ListAdapter<Cast, CastMovieAddAdapter.ViewHolder>(DiffCallback()) {

    var selectedIds: MutableList<String?> = initialSelectedIds.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCastMovieAddBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(val binding: ItemCastMovieAddBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cast) {
            val id = item.id
            val name = item.name
            val image = item.image ?: DATA.EMPTY

            binding.image.loadImage(image, isUser = true)

            if (name.isEmpty()) {
                binding.name.visibility = View.GONE
            } else {
                binding.name.visibility = View.VISIBLE
                binding.name.text = name
            }

            updateVisibility(id)

            binding.add.setOnClickListener {
                if (!selectedIds.contains(id)) {
                    selectedIds.add(id)
                    updateVisibility(id)
                }
            }

            binding.remove.setOnClickListener {
                if (selectedIds.contains(id)) {
                    selectedIds.remove(id)
                    updateVisibility(id)
                }
            }
        }

        private fun updateVisibility(id: String) {
            val isSelected = selectedIds.contains(id)
            binding.add.visibility = if (isSelected) View.GONE else View.VISIBLE
            binding.remove.visibility = if (isSelected) View.VISIBLE else View.GONE
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean = oldItem == newItem
    }
}