package com.flatcode.littlemovieadmin.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.Filter.CastFilter
import com.flatcode.littlemovieadmin.Model.Cast
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.databinding.ItemCastBinding
import java.text.MessageFormat

class CastAdapter(private val activity: Activity, var list: ArrayList<Cast?>) :
    RecyclerView.Adapter<CastAdapter.ViewHolder>(), Filterable {

    private var binding: ItemCastBinding? = null
    var filterList: ArrayList<Cast?>
    private var filter: CastFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemCastBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val aboutMy = DATA.EMPTY + item.aboutMy
        val interestedCount = DATA.EMPTY + item.interestedCount
        val moviesCount = DATA.EMPTY + item.moviesCount

        VOID.GlideImage(true, activity, image, holder.image)

        if (item.name == DATA.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = name
        }

        if (interestedCount == DATA.EMPTY) holder.numberInterested.text = MessageFormat.format(
            "{0}{1}", DATA.EMPTY, DATA.ZERO
        ) else holder.numberInterested.text = interestedCount

        if (moviesCount == DATA.EMPTY) holder.numberMovies.text = MessageFormat.format(
            "{0}{1}", DATA.EMPTY, DATA.ZERO
        ) else holder.numberMovies.text = moviesCount

        holder.more.setOnClickListener {
            VOID.moreDeleteCast(activity, item, DATA.NULL, DATA.NULL, DATA.NULL, true, false)
        }

        holder.item.setOnClickListener {
            VOID.IntentExtra4(
                activity, CLASS.CAST_DETAILS, DATA.CAST_ID, id, DATA.CAST_NAME,
                name, DATA.CAST_IMAGE, image, DATA.CAST_ABOUT, aboutMy
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

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(
        view!!
    ) {
        var image: ImageView
        var more: ImageButton
        var name: TextView
        var numberMovies: TextView
        var numberInterested: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            name = binding!!.name
            more = binding!!.more
            numberMovies = binding!!.numberMovies
            numberInterested = binding!!.numberInterested
            item = binding!!.item
        }
    }

    init {
        filterList = list
    }
}