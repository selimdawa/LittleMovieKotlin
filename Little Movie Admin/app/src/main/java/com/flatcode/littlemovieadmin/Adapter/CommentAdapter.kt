package com.flatcode.littlemovieadmin.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.Model.Comment
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.databinding.ItemCommentBinding
import com.flatcode.littlemovieadminimport.MyApplication
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CommentAdapter(private val context: Context, var list: ArrayList<Comment?>) :
    RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private var binding: ItemCommentBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemCommentBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
        val commentId = DATA.EMPTY + item!!.id
        val movieId = DATA.EMPTY + item.movieId
        val comment = DATA.EMPTY + item.comment
        val publisher = DATA.EMPTY + item.publisher
        val timestamp = DATA.EMPTY + item.timestamp
        val date: String = MyApplication.formatTimestamp(timestamp.toLong())

        holder.date.text = date
        holder.comment.text = comment
        loadUserDetails(publisher, holder.name, holder.image)

        holder.item.setOnClickListener {
            if (publisher == DATA.FirebaseUserUid) deleteComment(commentId, movieId)
        }
    }

    private fun deleteComment(commentId: String, movieId: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Delete Comment")
            .setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton("DELETE") { dialog: DialogInterface?, which: Int ->
                val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
                ref.child(movieId).child(DATA.COMMENTS).child(commentId).removeValue()
                    .addOnSuccessListener {
                        Toast.makeText(context, "Deleted...", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener { e: Exception ->
                        Toast.makeText(
                            context, "Failed to delete duo to " + e.message, Toast.LENGTH_SHORT
                        ).show()
                    }
            }
            .setNegativeButton("CANCEL") { dialog: DialogInterface, which: Int -> dialog.dismiss() }
            .show()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        var image: ImageView
        var name: TextView
        var comment: TextView
        var date: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            name = binding!!.name
            comment = binding!!.comment
            date = binding!!.date
            item = binding!!.item
        }
    }

    private fun loadUserDetails(publisher: String, name: TextView, image: ImageView) {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.USERS)
        ref.child(publisher).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val username = DATA.EMPTY + snapshot.child(DATA.USER_NAME).value
                val profileImage = DATA.EMPTY + snapshot.child(DATA.PROFILE_IMAGE).value

                VOID.GlideImage(true, context, profileImage, image)
                name.text = username
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}