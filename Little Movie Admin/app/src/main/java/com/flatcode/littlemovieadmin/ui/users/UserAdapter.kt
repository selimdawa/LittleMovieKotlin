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
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.filter.UserFilter
import com.flatcode.littlemovieadmin.model.User
import com.flatcode.littlemovieadmin.ui.profile.ProfileActivity
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.loadGlideImage
import com.flatcode.littlemovieadmin.utils.openActivity
import com.flatcode.littlemovieadmin.databinding.ItemUserBinding

class UserAdapter(private val context: Context) :
    ListAdapter<User, UserAdapter.ViewHolder>(DiffCallback()), Filterable {

    var filterList: List<User> = emptyList()
    private var filter: UserFilter? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val id = item.id
        val image = item.profileImage

        holder.binding.imageProfile.loadGlideImage(image, true)

        if (item.username == DATA.EMPTY) {
            holder.binding.username.visibility = View.GONE
        } else {
            holder.binding.username.visibility = View.VISIBLE
            holder.binding.username.text = item.username
        }

        holder.binding.item.setOnClickListener {
            context.openActivity<ProfileActivity>(extras = arrayOf(DATA.PROFILE_ID to id))
        }
    }

    override fun getFilter(): Filter {
        if (filter == null) {
            filter = UserFilter(filterList, this)
        }
        return filter!!
    }

    class ViewHolder(val binding: ItemUserBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.id == newItem.id && oldItem.username == newItem.username && 
            oldItem.profileImage == newItem.profileImage
    }
}
