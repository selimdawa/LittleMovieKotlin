package com.flatcode.littlemovie.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import coil3.load
import com.flatcode.littlemovie.databinding.ItemSliderBinding
import io.selimdawa.autoimageslider.adapter.SliderViewAdapter

class ImageSliderAdapter(private val images: List<String>, private val onItemClick: (Int) -> Unit) :
    SliderViewAdapter<ImageSliderAdapter.SliderViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SliderViewHolder(binding)
    }

    override fun onBind(viewHolder: SliderViewHolder, position: Int) {
        images.getOrNull(position)?.let { url ->
            viewHolder.binding.imageView.load(url)
            viewHolder.binding.imageView.setOnClickListener { onItemClick(position) }
        }
    }

    override fun getItemCount(): Int = images.size

    class SliderViewHolder(val binding: ItemSliderBinding) :
        ViewHolder(binding.root)
}
