package com.flatcode.littlemovieadmin.ui.cast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.databinding.ItemCastBinding
import com.flatcode.littlemovieadmin.model.Cast
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.loadImage

class CastAdapter(
    private val onItemClick: (Cast) -> Unit, private val onMoreClick: (Cast) -> Unit
) : ListAdapter<Cast, CastAdapter.ViewHolder>(DiffCallback()) {

    var list: List<Cast> = emptyList()
        set(value) {
            field = value
            submitList(value)
        }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            list
        } else {
            list.filter { it.name.contains(query, ignoreCase = true) }
        }
        submitList(filteredList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick, onMoreClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        val binding: ItemCastBinding,
        private val onItemClick: (Cast) -> Unit,
        private val onMoreClick: (Cast) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Cast) {
            val name = item.name
            val image = item.image ?: DATA.EMPTY
            val interestedCount = item.interestedCount
            val moviesCount = item.moviesCount

            binding.image.loadImage(image, isUser = true)

            if (name == DATA.EMPTY) {
                binding.name.visibility = View.GONE
            } else {
                binding.name.visibility = View.VISIBLE
                binding.name.text = name
            }

            binding.numberInterested.text = interestedCount.toString()
            binding.numberMovies.text = moviesCount.toString()

            binding.more.setOnClickListener { onMoreClick(item) }
            binding.item.setOnClickListener { onItemClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean = oldItem == newItem
    }
}
