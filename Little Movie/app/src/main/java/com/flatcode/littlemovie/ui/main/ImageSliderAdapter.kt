package com.flatcode.littlemovie.ui.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import coil3.load
import com.flatcode.littlemovie.databinding.ItemSliderBinding
import com.flatcode.littlemovie.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.smarteist.autoimageslider.SliderViewAdapter

class ImageSliderAdapter(private val images: List<String>, private val onItemClick: (Int) -> Unit) :
    SliderViewAdapter<ImageSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): SliderViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder, position: Int) {
        images.getOrNull(position)?.let { url ->
            viewHolder.binding.imageView.load(url)
            viewHolder.binding.imageView.setOnClickListener { onItemClick(position) }
        }
    }

    override fun getCount(): Int = images.size

    class SliderViewHolder(val binding: ItemSliderBinding) : ViewHolder(binding.root)
}
