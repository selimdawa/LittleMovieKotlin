package com.flatcode.littlemovie.Adapter

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
import com.flatcode.littlemovie.Filter.MovieFilter
import com.flatcode.littlemovie.Model.Movie
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.databinding.ItemMovieBinding
import java.text.MessageFormat

class MovieAdapter(private val context: Context?, var list: ArrayList<Movie?>, animation: Boolean) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>(), Filterable {

    private var binding: ItemMovieBinding? = null
    var filterList: ArrayList<Movie?>
    private var filter: MovieFilter? = null
    private val animation: Boolean

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemMovieBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val viewsCount = DATA.EMPTY + item.viewsCount
        val lovesCount = DATA.EMPTY + item.lovesCount
        val movieLink = DATA.EMPTY + item.movieLink

        VOID.GlideImage(false, context, image, holder.image)

        if (item.name == DATA.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = name
        }

        if (viewsCount == DATA.EMPTY) holder.numberViews.text =
            MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO) else holder.numberViews.text =
            viewsCount

        if (lovesCount == DATA.EMPTY) holder.numberLoves.text =
            MessageFormat.format("{0}{1}", DATA.EMPTY, DATA.ZERO) else holder.numberLoves.text =
            lovesCount

        VOID.isFavorite(holder.add, id, DATA.FirebaseUserUid)
        holder.add.setOnClickListener { VOID.checkFavorite(holder.add, id) }
        VOID.isLoves(holder.love, id)
        VOID.nrLoves(holder.numberLoves, id)
        holder.love.setOnClickListener { VOID.checkLove(holder.love, id) }
        if (animation) holder.item.animation = AnimationUtils.loadAnimation(
            context, R.anim.fade_transition_animation
        )

        holder.item.setOnClickListener {
            VOID.IntentExtra2(
                context, CLASS.MOVIE_DETAILS, DATA.MOVIE_ID, id, DATA.MOVIE_LINK, movieLink
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

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView
        var add: ImageView
        var love: ImageView
        var name: TextView
        var numberViews: TextView
        var numberLoves: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            love = binding!!.love
            add = binding!!.add
            name = binding!!.name
            numberViews = binding!!.nrViews
            numberLoves = binding!!.nrLoves
            item = binding!!.item
        }
    }

    init {
        filterList = list
        this.animation = animation
    }
}