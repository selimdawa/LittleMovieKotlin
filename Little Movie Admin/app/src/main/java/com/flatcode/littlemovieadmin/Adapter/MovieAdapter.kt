package com.flatcode.littlemovieadmin.Adapter

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
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.Filter.MovieFilter
import com.flatcode.littlemovieadmin.Model.Movie
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.databinding.ItemMovieBinding
import java.text.MessageFormat

class MovieAdapter(private val activity: Activity, var list: ArrayList<Movie?>) :
    RecyclerView.Adapter<MovieAdapter.ViewHolder>(), Filterable {

    private var binding: ItemMovieBinding? = null
    var filterList: ArrayList<Movie?>
    private var filter: MovieFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemMovieBinding.inflate(LayoutInflater.from(activity), parent, false)
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
        val categoryId = DATA.EMPTY + item.categoryId

        VOID.GlideImage(false, activity, image, holder.image)

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

        VOID.isFavorite(holder.add, item.id, DATA.FirebaseUserUid)
        holder.add.setOnClickListener { VOID.checkFavorite(holder.add, id) }

        holder.item.animation =
            AnimationUtils.loadAnimation(activity, R.anim.fade_transition_animation)

        holder.more.setOnClickListener {
            VOID.moreDeleteMovie(
                activity, item, DATA.CATEGORIES, categoryId, DATA.MOVIES_COUNT, false, true
            )
        }
        holder.item.setOnClickListener {
            VOID.IntentExtra2(
                activity, CLASS.MOVIE_DETAILS, DATA.MOVIE_ID, id, DATA.MOVIE_LINK, movieLink
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
        var more: ImageButton
        var name: TextView
        var numberViews: TextView
        var numberLoves: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            add = binding!!.add
            name = binding!!.name
            more = binding!!.more
            numberViews = binding!!.nrViews
            numberLoves = binding!!.nrLoves
            item = binding!!.item
        }
    }

    init {
        filterList = list
    }
}