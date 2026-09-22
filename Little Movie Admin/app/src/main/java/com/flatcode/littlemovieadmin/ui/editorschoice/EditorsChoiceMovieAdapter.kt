package com.flatcode.littlemovieadmin.ui.editorschoice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.databinding.ItemEditorsChoiceBinding
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.loadImage

class EditorsChoiceMovieAdapter(
    private val onAddClick: (Movie) -> Unit
) : ListAdapter<Movie, EditorsChoiceMovieAdapter.ViewHolder>(DiffCallback()) {

    var list: List<Movie> = emptyList()

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            list
        } else {
            list.filter { it.name.contains(query, ignoreCase = true) }
        }
        submitList(filteredList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEditorsChoiceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, onAddClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        val binding: ItemEditorsChoiceBinding,
        private val onAddClick: (Movie) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Movie) {
            val name = item.name
            val image = item.image ?: DATA.EMPTY
            val nrViews = item.viewsCount
            val nrLoves = item.lovesCount

            binding.image.loadImage(image, false)

            if (name == DATA.EMPTY) {
                binding.name.visibility = View.GONE
            } else {
                binding.name.visibility = View.VISIBLE
                binding.name.text = name
            }

            binding.nrViews.text = nrViews.toString()
            binding.nrLoves.text = nrLoves.toString()

            binding.add.setOnClickListener { onAddClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }
}
