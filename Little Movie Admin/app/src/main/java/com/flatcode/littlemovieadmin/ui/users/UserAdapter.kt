package com.flatcode.littlemovieadmin.ui.users

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.databinding.ItemUserBinding
import com.flatcode.littlemovieadmin.model.User
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.loadImage

class UserAdapter(
    private val onItemClick: (User) -> Unit
) : ListAdapter<User, UserAdapter.ViewHolder>(DiffCallback()) {

    var list: List<User> = emptyList()
        set(value) {
            field = value
            submitList(value)
        }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            list
        } else {
            list.filter { it.username?.contains(query, ignoreCase = true) == true }
        }
        submitList(filteredList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        val binding: ItemUserBinding,
        private val onItemClick: (User) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: User) {
            val image = item.profileImage ?: DATA.EMPTY
            val username = item.username ?: DATA.EMPTY

            binding.imageProfile.loadImage(image, true)

            if (username == DATA.EMPTY) {
                binding.username.visibility = View.GONE
            } else {
                binding.username.visibility = View.VISIBLE
                binding.username.text = username
            }

            binding.item.setOnClickListener { onItemClick(item) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean =
            oldItem.id == newItem.id && oldItem.username == newItem.username && 
            oldItem.profileImage == newItem.profileImage
    }
}
