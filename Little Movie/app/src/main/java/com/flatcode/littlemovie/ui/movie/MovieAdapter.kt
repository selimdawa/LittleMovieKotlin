package com.flatcode.littlemovie.ui.movie

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.databinding.ItemMovieBinding
import com.flatcode.littlemovie.model.Movie
import com.flatcode.littlemovie.utils.*
import java.util.Locale

class MovieAdapter(
    private val animation: Boolean = false,
    private val onItemClick: (Movie) -> Unit
) : ListAdapter<Movie, MovieAdapter.ViewHolder>(DiffCallback) {

    private var fullList: List<Movie> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), animation, onItemClick)
    }

    fun setFullList(list: List<Movie>) {
        fullList = list
        submitList(list)
    }

    fun filter(query: String?) {
        if (query.isNullOrEmpty()) {
            submitList(fullList)
        } else {
            val q = query.uppercase(Locale.getDefault())
            val filtered = fullList.filter {
                it.name?.uppercase(Locale.getDefault())?.contains(q) == true
            }
            submitList(filtered)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }

    class ViewHolder(private val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Movie, animation: Boolean, onItemClick: (Movie) -> Unit) {
            val id = item.id
            val name = item.name ?: ""
            val image = item.image ?: ""
            val viewsCount = item.viewsCount.toString()
            val lovesCount = item.lovesCount.toString()

            with(binding) {
                this.image.loadImage(false, image)

                if (name.isEmpty()) {
                    this.name.visibility = View.GONE
                } else {
                    this.name.visibility = View.VISIBLE
                    this.name.text = name
                }

                nrViews.text = viewsCount
                nrLoves.text = lovesCount

                add.isFavorite(id, DATA.FirebaseUserUid)
                add.setOnClickListener { add.checkFavorite(id) }
                love.isLoves(id)
                nrLoves.nrLoves(id)
                love.setOnClickListener { love.checkLove(id) }

                if (animation) {
                    this.item.animation = AnimationUtils.loadAnimation(
                        root.context, R.anim.fade_transition_animation
                    )
                }

                this.item.setOnClickListener { onItemClick(item) }
            }
        }
    }
}
