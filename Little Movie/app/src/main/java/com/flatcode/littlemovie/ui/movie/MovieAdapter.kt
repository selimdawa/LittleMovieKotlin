package com.flatcode.littlemovie.ui.movie

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.filter.MovieFilter
import com.flatcode.littlemovie.model.Movie
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.GlideImage
import com.flatcode.littlemovie.utils.checkFavorite
import com.flatcode.littlemovie.utils.checkLove
import com.flatcode.littlemovie.utils.isFavorite
import com.flatcode.littlemovie.utils.isLoves
import com.flatcode.littlemovie.utils.nrLoves
import com.flatcode.littlemovie.utils.openActivity
import com.flatcode.littlemovie.databinding.ItemMovieBinding
import java.text.MessageFormat

class MovieAdapter(private val context: Context?, var list: ArrayList<Movie?>, animation: Boolean) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>(), Filterable {

    var filterList: ArrayList<Movie?>
    private var filter: MovieFilter? = null
    private val animation: Boolean

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val viewsCount = DATA.EMPTY + item.viewsCount
        val lovesCount = DATA.EMPTY + item.lovesCount
        val movieLink = DATA.EMPTY + item.movieLink

        holder.binding.image.GlideImage(false, image)

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

        holder.binding.add.isFavorite(id, DATA.FirebaseUserUid)
        holder.binding.add.setOnClickListener { holder.binding.add.checkFavorite(id) }
        holder.binding.love.isLoves(id)
        holder.binding.nrLoves.nrLoves(id)
        holder.binding.love.setOnClickListener { holder.binding.love.checkLove(id) }
        if (animation) holder.binding.item.animation = AnimationUtils.loadAnimation(
            context, R.anim.fade_transition_animation
        )

        holder.binding.item.setOnClickListener {
            context?.openActivity<MovieDetailsActivity>(
                DATA.MOVIE_ID to id, DATA.MOVIE_LINK to movieLink
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = MovieFilter(filterList, this)
        }
        return filter!!
    }

    inner class ViewHolder(val binding: ItemMovieBinding) : RecyclerView.ViewHolder(binding.root)

    init {
        filterList = list
        this.animation = animation
    }
}
