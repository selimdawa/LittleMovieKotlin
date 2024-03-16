package com.flatcode.littlemovieadmin.Adapter

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
import com.flatcode.littlemovieadmin.Filter.EditorsChoiceFilter
import com.flatcode.littlemovieadmin.Model.Movie
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.databinding.ItemEditorsChoiceBinding

class EditorsChoiceMovieAdapter(
    private val activity: Activity, var oldId: String?, var list: ArrayList<Movie?>, number: Int
) : RecyclerView.Adapter<EditorsChoiceMovieAdapter.ViewHolder>(), Filterable {

    private var binding: ItemEditorsChoiceBinding? = null
    var filterList: ArrayList<Movie?>
    private var filter: EditorsChoiceFilter? = null
    var number: Int

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemEditorsChoiceBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image
        val nrViews = DATA.EMPTY + item.viewsCount
        val nrLoves = DATA.EMPTY + item.lovesCount

        VOID.GlideImage(false, activity, image, holder.image)

        if (name == DATA.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = name
        }

        holder.nrViews.text = nrViews
        holder.nrLoves.text = nrLoves

        holder.add.setOnClickListener {
            if (oldId != null) {
                VOID.addToEditorsChoice(activity, activity, id, number)
                VOID.addToEditorsChoice(activity, activity, oldId, 0)
            } else {
                VOID.addToEditorsChoice(activity, activity, id, number)
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = EditorsChoiceFilter(filterList, this)
        }
        return filter!!
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var add: ImageView
        var image: ImageView
        var name: TextView
        var nrViews: TextView
        var nrLoves: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            name = binding!!.name
            nrViews = binding!!.nrViews
            nrLoves = binding!!.nrLoves
            add = binding!!.add
            item = binding!!.item
        }
    }

    init {
        filterList = list
        this.number = number
    }
}