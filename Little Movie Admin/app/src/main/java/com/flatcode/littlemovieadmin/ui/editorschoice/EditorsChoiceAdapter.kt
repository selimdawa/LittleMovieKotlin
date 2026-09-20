package com.flatcode.littlemovieadmin.ui.editorschoice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.databinding.ItemMovieEditorsChoiceBinding
import com.flatcode.littlemovieadmin.model.EditorsChoice
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.loadGlideImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class EditorsChoiceAdapter(
    private val onAddClick: (EditorsChoice) -> Unit,
    private val onChangeClick: (EditorsChoice, String) -> Unit,
    private val onDeleteClick: (String, String) -> Unit
) : ListAdapter<EditorsChoice, EditorsChoiceAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMovieEditorsChoiceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding, onAddClick, onChangeClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(
        val binding: ItemMovieEditorsChoiceBinding,
        private val onAddClick: (EditorsChoice) -> Unit,
        private val onChangeClick: (EditorsChoice, String) -> Unit,
        private val onDeleteClick: (String, String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(model: EditorsChoice) {
            val id = model.id
            binding.numberEditorsChoice.text = id.toString()

            loadMovieDetails(model)

            binding.add.setOnClickListener { onAddClick(model) }
        }

        private fun loadMovieDetails(model: EditorsChoice) {
            val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
            ref.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var movieFound = false
                    for (snapshot in dataSnapshot.children) {
                        val item = snapshot.getValue(Movie::class.java) ?: continue
                        if (item.editorsChoice == model.id) {
                            movieFound = true
                            displayMovieDetails(item, model)
                            break
                        }
                    }
                    if (!movieFound) {
                        displayEmpty()
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        }

        private fun displayMovieDetails(item: Movie, model: EditorsChoice) {
            val id = item.id
            val name = item.name
            val imageLink = item.image ?: DATA.EMPTY
            val viewsCount = item.viewsCount
            val lovesCount = item.lovesCount

            binding.image.loadGlideImage(imageLink, false)
            binding.name.text = name
            binding.nrViews.text = viewsCount.toString()
            binding.nrLoves.text = lovesCount.toString()

            binding.addCard.visibility = View.GONE
            binding.detailsCard.visibility = View.VISIBLE
            binding.remove.visibility = View.VISIBLE
            binding.change.visibility = View.VISIBLE

            binding.remove.setOnClickListener { onDeleteClick(id, name) }
            binding.change.setOnClickListener { onChangeClick(model, id) }
        }

        private fun displayEmpty() {
            binding.addCard.visibility = View.VISIBLE
            binding.detailsCard.visibility = View.GONE
            binding.remove.visibility = View.GONE
            binding.change.visibility = View.GONE
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<EditorsChoice>() {
        override fun areItemsTheSame(oldItem: EditorsChoice, newItem: EditorsChoice): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: EditorsChoice, newItem: EditorsChoice): Boolean =
            oldItem == newItem
    }
}
