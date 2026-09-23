package com.flatcode.littlemovie.ui.cast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.databinding.ItemCastBinding
import com.flatcode.littlemovie.model.Cast
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.checkInterested
import com.flatcode.littlemovie.utils.isInterested
import com.flatcode.littlemovie.utils.loadImage
import java.util.Locale

class CastAdapter(private val onItemClick: (Cast) -> Unit) :
    ListAdapter<Cast, CastAdapter.ViewHolder>(DiffCallback) {

    private var fullList: List<Cast> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCastBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    fun setFullList(list: List<Cast>) {
        fullList = list
        submitList(list)
    }

    fun filter(query: String?) {
        if (query.isNullOrEmpty()) {
            submitList(fullList)
        } else {
            val q = query.uppercase(Locale.getDefault())
            val filtered = fullList.filter {
                it.name?.uppercase(Locale.getDefault())?.contains(q) == true
            }
            submitList(filtered)
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Cast>() {
        override fun areItemsTheSame(oldItem: Cast, newItem: Cast): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Cast, newItem: Cast): Boolean = oldItem == newItem
    }

    class ViewHolder(private val binding: ItemCastBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Cast, onItemClick: (Cast) -> Unit) {
            val id = item.id
            val name = item.name ?: ""
            val image = item.image ?: ""
            val interestedCount = item.interestedCount.toString()
            val moviesCount = item.moviesCount.toString()

            with(binding) {
                this.image.loadImage(true, image)

                if (name.isEmpty()) {
                    this.name.visibility = View.GONE
                } else {
                    this.name.visibility = View.VISIBLE
                    this.name.text = name
                }

                numberInterested.text = interestedCount
                numberMovies.text = moviesCount

                add.isInterested(id, DATA.CAST)
                add.setOnClickListener { add.checkInterested(DATA.CAST, id) }

                this.item.setOnClickListener { onItemClick(item) }
            }
        }
    }
}
