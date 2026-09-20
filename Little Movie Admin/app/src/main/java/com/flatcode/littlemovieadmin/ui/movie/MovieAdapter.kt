package com.flatcode.littlemovieadmin.ui.movie

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.filter.MovieFilter
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.checkFavorite
import com.flatcode.littlemovieadmin.utils.openActivity
import com.flatcode.littlemovieadmin.utils.isFavorite
import com.flatcode.littlemovieadmin.utils.loadGlideImage
import com.flatcode.littlemovieadmin.utils.moreDeleteMovie
import com.flatcode.littlemovieadmin.databinding.ItemMovieBinding
import java.text.MessageFormat

class MovieAdapter(private val activity: Activity) :
    ListAdapter<Movie, MovieAdapter.ViewHolder>(DiffCallback()), Filterable {

    var filterList: List<Movie> = emptyList()
    private var filter: MovieFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val id = DATA.EMPTY + item.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val viewsCount = DATA.EMPTY + item.viewsCount
        val lovesCount = DATA.EMPTY + item.lovesCount
        val movieLink = DATA.EMPTY + item.movieLink
        val categoryId = DATA.EMPTY + item.categoryId

        holder.binding.image.loadGlideImage(image, false)

        if (item.name == DATA.EMPTY) {
            holder.binding.name.visibility = View.GONE
        } else {
            holder.binding.name.visibility = View.VISIBLE
            holder.binding.name.text = name
        }

        if (viewsCount == DATA.EMPTY) holder.binding.nrViews.text =
            MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO) else holder.binding.nrViews.text =
            viewsCount

        if (lovesCount == DATA.EMPTY) holder.binding.nrLoves.text =
            MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO) else holder.binding.nrLoves.text =
            lovesCount

        holder.binding.add.isFavorite(item.id, DATA.FirebaseUserUid)
        holder.binding.add.setOnClickListener { holder.binding.add.checkFavorite(id) }

        holder.binding.item.animation =
            AnimationUtils.loadAnimation(activity, R.anim.fade_transition_animation)

        holder.binding.more.setOnClickListener {
            activity.moreDeleteMovie(
                item, DATA.CATEGORIES, categoryId, DATA.MOVIES_COUNT, false, true
            )
        }
        holder.binding.item.setOnClickListener {
            activity.openActivity<MovieDetailsActivity>(
                extras = arrayOf(
                    DATA.MOVIE_ID to id, DATA.MOVIE_LINK to movieLink
                )
            )
        }
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = MovieFilter(filterList, this)
        }
        return filter!!
    }

    class ViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean =
            oldItem == newItem
    }
}
