package com.flatcode.littlemovie.filter

import android.widget.Filter
import com.flatcode.littlemovie.model.Cast
import com.flatcode.littlemovie.ui.cast.CastAdapter
import java.util.*

class CastFilter(private val list: List<Cast>, private val adapter: CastAdapter) : Filter() {
    override fun performFiltering(constraint: CharSequence?): FilterResults {
        val results = FilterResults()
        if (!constraint.isNullOrEmpty()) {
            val query = constraint.toString().uppercase(Locale.getDefault())
            val filtered = list.filter {
                it.name?.uppercase(Locale.getDefault())?.contains(query) == true
            }
            results.count = filtered.size
            results.values = filtered
        } else {
            results.count = list.size
            results.values = list
        }
        return results
    }

    @Suppress("UNCHECKED_CAST")
    override fun publishResults(constraint: CharSequence?, results: FilterResults) {
        adapter.submitList(results.values as? List<Cast>)
    }
}
