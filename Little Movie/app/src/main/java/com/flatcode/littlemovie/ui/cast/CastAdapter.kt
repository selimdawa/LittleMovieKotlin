package com.flatcode.littlemovie.ui.cast

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.filter.CastFilter
import com.flatcode.littlemovie.model.Cast
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.GlideImage
import com.flatcode.littlemovie.utils.checkInterested
import com.flatcode.littlemovie.utils.isInterested
import com.flatcode.littlemovie.utils.openActivity
import com.flatcode.littlemovie.databinding.ItemCastBinding
import java.text.MessageFormat

class CastAdapter(private val activity: Activity, var list: ArrayList<Cast?>) :
    RecyclerView.Adapter<CastAdapter.ViewHolder>(), Filterable {

    var filterList: ArrayList<Cast?>
    private var filter: CastFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCastBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val aboutMy = DATA.EMPTY + item.aboutMy
        val interestedCount = DATA.EMPTY + item.interestedCount
        val moviesCount = DATA.EMPTY + item.moviesCount

        holder.binding.image.GlideImage(true, image)

        if (item.name == DATA.EMPTY) {
            holder.binding.name.visibility = View.GONE
        } else {
            holder.binding.name.visibility = View.VISIBLE
            holder.binding.name.text = name
        }

        if (interestedCount == DATA.EMPTY) holder.binding.numberInterested.text = MessageFormat.format(
            "{0}{1}", DATA.EMPTY, DATA.ZERO
        ) else holder.binding.numberInterested.text = interestedCount

        if (moviesCount == DATA.EMPTY) holder.binding.numberMovies.text = MessageFormat.format(
            "{0}{1}", DATA.EMPTY, DATA.ZERO
        ) else holder.binding.numberMovies.text = moviesCount

        holder.binding.add.isInterested(id, DATA.CAST)
        holder.binding.add.setOnClickListener { holder.binding.add.checkInterested(DATA.CAST, id) }

        holder.binding.item.setOnClickListener {
            activity.openActivity<CastDetailsActivity>(
                DATA.CAST_ID to id, DATA.CAST_NAME to name,
                DATA.CAST_IMAGE to image, DATA.CAST_ABOUT to aboutMy
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = CastFilter(filterList, this)
        }
        return filter!!
    }

    inner class ViewHolder(val binding: ItemCastBinding) : RecyclerView.ViewHolder(binding.root)

    init {
        filterList = list
    }
}
