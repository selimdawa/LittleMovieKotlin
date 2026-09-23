package com.flatcode.littlemovie.ui.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.Application
import com.flatcode.littlemovie.databinding.ItemCommentBinding
import com.flatcode.littlemovie.model.Comment
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.loadImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommentAdapter(private val onDeleteClick: (Comment) -> Unit) :
    ListAdapter<Comment, CommentAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onDeleteClick)
    }

    object DiffCallback : DiffUtil.ItemCallback<Comment>() {
        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean =
            oldItem.id == newItem.id && oldItem.comment == newItem.comment
    }

    class ViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Comment, onDeleteClick: (Comment) -> Unit) {
            val publisher = item.publisher ?: ""
            val comment = item.comment ?: ""
            val date: String = Application.formatTimestamp(item.timestamp)

            with(binding) {
                this.date.text = date
                this.comment.text = comment
                loadUserDetails(publisher, name, image)

                this.item.setOnClickListener {
                    if (publisher == DATA.FirebaseUserUid) onDeleteClick(item)
                }
            }
        }

        private fun loadUserDetails(publisher: String, name: TextView, image: ImageView) {
            FirebaseDatabase.getInstance().getReference(DATA.USERS).child(publisher)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val username = snapshot.child(DATA.USER_NAME).value?.toString() ?: ""
                        val profileImage =
                            snapshot.child(DATA.PROFILE_IMAGE).value?.toString() ?: ""
                        image.loadImage(true, profileImage)
                        name.text = username
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        }
    }
}
