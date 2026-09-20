package com.flatcode.littlemovieadmin.ui.main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.model.Main
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.utils.DATA

import com.flatcode.littlemovieadmin.databinding.ItemMainBinding
import java.text.MessageFormat

class MainAdapter(private val context: Context) :
    ListAdapter<Main, MainAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMainBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = getItem(position)
        val image = model.image
        val number = model.number
        val name = model.title
        val c = model.c

        if (image != 0) {
            holder.binding.image.setImageResource(image)
        } else {
            holder.binding.image.setImageResource(R.drawable.ic_load)
        }

        if (number != 0) {
            holder.binding.number.visibility = View.VISIBLE
            holder.binding.number.text = MessageFormat.format("{0}{1}", DATA.EMPTY, number)
        } else {
            holder.binding.number.visibility = View.GONE
        }

        holder.binding.name.text = name

        holder.itemView.setOnClickListener {
            if (c != null) {
                val intent = Intent(context, c)
                context.startActivity(intent)
            }
        }
    }

    class ViewHolder(val binding: ItemMainBinding) : RecyclerView.ViewHolder(binding.root)

    class DiffCallback : DiffUtil.ItemCallback<Main>() {
        override fun areItemsTheSame(oldItem: Main, newItem: Main): Boolean =
            oldItem.title == newItem.title

        override fun areContentsTheSame(oldItem: Main, newItem: Main): Boolean =
            oldItem.title == newItem.title && oldItem.image == newItem.image && 
            oldItem.number == newItem.number
    }
}
