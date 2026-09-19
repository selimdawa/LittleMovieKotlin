package com.flatcode.littlemovieadmin.ui.users

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.filter.UserFilter
import com.flatcode.littlemovieadmin.model.User
import com.flatcode.littlemovieadmin.ui.profile.ProfileActivity
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.VOID
import com.flatcode.littlemovieadmin.databinding.ItemUserBinding

class UserAdapter(private val context: Context, var list: ArrayList<User?>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>(), Filterable {

    private var binding: ItemUserBinding? = null
    var filterList: ArrayList<User?>
    private var filter: UserFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val id = item!!.id
        val image = item.profileImage

        VOID.GlideImage(true, context, image, holder.image)

        if (item.username == DATA.EMPTY) {
            holder.username.visibility = View.GONE
        } else {
            holder.username.visibility = View.VISIBLE
            holder.username.text = item.username
        }

        holder.item.setOnClickListener {
            VOID.IntentExtra(context, ProfileActivity::class.java, DATA.PROFILE_ID, id)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = UserFilter(filterList, this)
        }
        return filter!!
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView
        var username: TextView
        var item: LinearLayout

        init {
            image = binding!!.imageProfile
            username = binding!!.username
            item = binding!!.item
        }
    }

    init {
        filterList = list
    }
}
