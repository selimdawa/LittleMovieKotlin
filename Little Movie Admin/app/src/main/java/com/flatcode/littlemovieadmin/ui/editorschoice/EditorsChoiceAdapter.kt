package com.flatcode.littlemovieadmin.ui.editorschoice

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.databinding.ItemMovieEditorsChoiceBinding
import com.flatcode.littlemovieadmin.model.EditorsChoice
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.dialogOptionDelete
import com.flatcode.littlemovieadmin.utils.openActivity
import com.flatcode.littlemovieadmin.utils.loadGlideImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class EditorsChoiceAdapter(private val activity: Activity) :
    ListAdapter<EditorsChoice, EditorsChoiceAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemMovieEditorsChoiceBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = getItem(position)
        val id = model.id
        val editorsChoiceId = DATA.EMPTY + id

        loadMovieDetails(
            id, editorsChoiceId, holder.binding.name, holder.binding.image, holder.binding.nrLoves,
            holder.binding.nrViews, holder.binding.remove, holder.binding.change, holder.binding.addCard, holder.binding.detailsCard
        )
        holder.binding.numberEditorsChoice.text = MessageFormat.format("{0}{1}", DATA.EMPTY, id)

        holder.binding.add.setOnClickListener {
            activity.openActivity<EditorsChoiceAddActivity>(
                extras = arrayOf(
                    DATA.EDITORS_CHOICE_ID to editorsChoiceId, DATA.OLD_ID to null
                )
            )
        }
    }

    class ViewHolder(val binding: ItemMovieEditorsChoiceBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<EditorsChoice>() {
        override fun areItemsTheSame(oldItem: EditorsChoice, newItem: EditorsChoice): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EditorsChoice, newItem: EditorsChoice): Boolean =
            oldItem == newItem
    }

    private fun loadMovieDetails(
        i: Int,
        position: String,
        title: TextView,
        imageView: ImageView,
        viewsCount: TextView,
        lovesCount: TextView,
        remove: ImageView,
        change: ImageView,
        addCard: CardView,
        detailsCard: CardView,
    ) {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val item = snapshot.getValue(Movie::class.java)!!
                    if (item.editorsChoice == i) {
                        val id = DATA.EMPTY + item.id
                        val name = DATA.EMPTY + item.name
                        loadData(id)
                        addCard.visibility = View.GONE
                        detailsCard.visibility = View.VISIBLE
                        remove.visibility = View.VISIBLE
                        change.visibility = View.VISIBLE
                        remove.setOnClickListener {
                            activity.dialogOptionDelete(
                                id, name, DATA.EDITORS_CHOICE, DATA.EDITORS_CHOICE,
                                true, DATA.NULL, DATA.NULL, DATA.NULL, false, false,
                            )
                        }
                        change.setOnClickListener {
                            activity.openActivity<EditorsChoiceAddActivity>(
                                extras = arrayOf(
                                    DATA.EDITORS_CHOICE_ID to position, DATA.OLD_ID to id
                                )
                            )
                        }
                    } else {
                        addCard.visibility = View.VISIBLE
                        detailsCard.visibility = View.GONE
                        remove.visibility = View.GONE
                        change.visibility = View.GONE
                    }
                }
            }

            private fun loadData(id: String) {
                val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
                ref.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        //get data
                        val item = dataSnapshot.getValue(Movie::class.java)!!
                        val name = DATA.EMPTY + item.name
                        val imageLink = DATA.EMPTY + item.image
                        val ViewsCount = DATA.EMPTY + item.viewsCount
                        val LovesCount = DATA.EMPTY + item.lovesCount

                        imageView.loadGlideImage(imageLink, false)
                        title.text = name
                        viewsCount.text = ViewsCount
                        lovesCount.text = LovesCount
                        addCard.visibility = View.GONE
                        detailsCard.visibility = View.VISIBLE
                        remove.visibility = View.VISIBLE
                        change.visibility = View.VISIBLE
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}
