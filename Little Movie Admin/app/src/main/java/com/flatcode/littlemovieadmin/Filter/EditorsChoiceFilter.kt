package com.flatcode.littlemovieadmin.Filter

import android.widget.Filter
import com.flatcode.littlemovieadmin.Adapter.EditorsChoiceMovieAdapter
import com.flatcode.littlemovieadmin.Model.Movie
import java.util.*

class EditorsChoiceFilter(var list: ArrayList<Movie?>, var adapter: EditorsChoiceMovieAdapter) :
    Filter() {
    override fun performFiltering(constraint: CharSequence): FilterResults {
        var constraint: CharSequence? = constraint
        val results = FilterResults()
        if (constraint != null && constraint.length > 0) {
            constraint = constraint.toString().uppercase(Locale.getDefault())
            val filter = ArrayList<Movie?>()
            for (i in list.indices) {
                if (list[i]!!.name!!.uppercase(Locale.getDefault()).contains(constraint)) {
                    filter.add(list[i])
                }
            }
            results.count = filter.size
            results.values = filter
        } else {
            results.count = list.size
            results.values = list
        }
        return results
    }

    override fun publishResults(constraint: CharSequence, results: FilterResults) {
        adapter.list = (results.values as ArrayList<Movie?>)
        adapter.notifyDataSetChanged()
    }
}