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
import com.flatcode.littlemovie.utils.VOID
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

        VOID.GlideImage(false, context, image, holder.binding.image)

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

        VOID.isFavorite(holder.binding.add, id, DATA.FirebaseUserUid)
        holder.binding.add.setOnClickListener { VOID.checkFavorite(holder.binding.add, id) }
        VOID.isLoves(holder.binding.love, id)
        VOID.nrLoves(holder.binding.nrLoves, id)
        holder.binding.love.setOnClickListener { VOID.checkLove(holder.binding.love, id) }
        if (animation) holder.binding.item.animation = AnimationUtils.loadAnimation(
            context, R.anim.fade_transition_animation
        )

        holder.binding.item.setOnClickListener {
            VOID.IntentExtra2(
                context, MovieDetailsActivity::class.java, DATA.MOVIE_ID, id, DATA.MOVIE_LINK, movieLink
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
