package com.flatcode.littlemovieadmin.Filter

import android.widget.Filter
import com.flatcode.littlemovieadmin.Adapter.UserAdapter
import com.flatcode.littlemovieadmin.Model.User
import java.util.*

class UserFilter(var list: ArrayList<User?>, var adapter: UserAdapter) : Filter() {
    override fun performFiltering(constraint: CharSequence): FilterResults {
        var constraint: CharSequence? = constraint
        val results = FilterResults()
        if (constraint != null && constraint.length > 0) {
            constraint = constraint.toString().uppercase(Locale.getDefault())
            val filter = ArrayList<User?>()
            for (i in list.indices) {
                if (list[i]!!.username!!.uppercase(Locale.getDefault()).contains(constraint)) {
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
        adapter.list = (results.values as ArrayList<User?>)
        adapter.notifyDataSetChanged()
    }
}