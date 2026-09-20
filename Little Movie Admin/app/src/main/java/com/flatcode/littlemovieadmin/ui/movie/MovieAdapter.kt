package com.flatcode.littlemovieadmin.ui.movie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ItemMovieBinding
import com.flatcode.littlemovieadmin.filter.MovieFilter
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.isFavorite
import com.flatcode.littlemovieadmin.utils.loadGlideImage

class MovieAdapter(
    private val onItemClick: (Movie) -> Unit,
    private val onMoreClick: (Movie) -> Unit,
    private val onFavoriteClick: (Movie, ImageView) -> Unit
) : ListAdapter<Movie, MovieAdapter.ViewHolder>(DiffCallback()), Filterable {

    var filterList: List<Movie> = emptyList()
    private var filter: MovieFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick, onMoreClick, onFavoriteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = MovieFilter(filterList, this)
        }
        return filter!!
    }

    class ViewHolder(
        val binding: ItemMovieBinding,
        private val onItemClick: (Movie) -> Unit,
        private val onMoreClick: (Movie) -> Unit,
        private val onFavoriteClick: (Movie, ImageView) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Movie) {
            val name = item.name
            val image = item.image ?: DATA.EMPTY
            val viewsCount = item.viewsCount
            val lovesCount = item.lovesCount

            binding.image.loadGlideImage(image, false)

            if (name == DATA.EMPTY) {
                binding.name.visibility = View.GONE
            } else {
                binding.name.visibility = View.VISIBLE
                binding.name.text = name
            }

            binding.nrViews.text = viewsCount.toString()
            binding.nrLoves.text = lovesCount.toString()

            binding.add.isFavorite(item.id, DATA.FirebaseUserUid)
            binding.add.setOnClickListener { onFavoriteClick(item, binding.add) }

            binding.item.animation =
                AnimationUtils.loadAnimation(binding.root.context, R.anim.fade_transition_animation)

            binding.more.setOnClickListener { onMoreClick(item) }
            binding.item.setOnClickListener { onItemClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }
}
