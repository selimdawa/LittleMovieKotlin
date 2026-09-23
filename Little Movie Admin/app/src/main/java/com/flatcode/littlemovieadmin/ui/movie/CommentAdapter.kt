package com.flatcode.littlemovieadmin.ui.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.Application
import com.flatcode.littlemovieadmin.databinding.ItemCommentBinding
import com.flatcode.littlemovieadmin.model.Comment
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.loadImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommentAdapter(
    private val onItemClick: (Comment) -> Unit
) : ListAdapter<Comment, CommentAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        val binding: ItemCommentBinding, private val onItemClick: (Comment) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Comment) {
            val comment = item.comment ?: DATA.EMPTY
            val publisher = item.publisher ?: DATA.EMPTY
            val timestamp = item.timestamp
            val date = Application.formatTimestamp(timestamp)

            binding.date.text = date
            binding.comment.text = comment
            loadUserDetails(publisher, binding.name, binding.image)

            binding.item.setOnClickListener {
                onItemClick(item)
            }
        }

        private fun loadUserDetails(publisher: String, name: TextView, image: ImageView) {
            if (publisher.isEmpty()) return
            val ref = FirebaseDatabase.getInstance().getReference(DATA.USERS)
            ref.child(publisher).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val username = DATA.EMPTY + snapshot.child(DATA.USER_NAME).value
                    val profileImage = DATA.EMPTY + snapshot.child(DATA.PROFILE_IMAGE).value

                    image.loadImage(profileImage, isUser = true)
                    name.text = username
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem.id == newItem.id && oldItem.comment == newItem.comment && oldItem.timestamp == newItem.timestamp
    }
}