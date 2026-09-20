package com.flatcode.littlemovie.ui.settings

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.databinding.ItemSettingBinding
import com.flatcode.littlemovie.model.Setting
import com.flatcode.littlemovie.utils.dialogAboutApp
import com.flatcode.littlemovie.utils.dialogLogout
import com.flatcode.littlemovie.utils.rateApp
import com.flatcode.littlemovie.utils.shareApp

class SettingAdapter(private val onItemClick: (Setting) -> Unit) :
    ListAdapter<Setting, SettingAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSettingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    object DiffCallback : DiffUtil.ItemCallback<Setting>() {
        override fun areItemsTheSame(oldItem: Setting, newItem: Setting): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Setting, newItem: Setting): Boolean =
            oldItem.id == newItem.id && oldItem.name == newItem.name && oldItem.number == newItem.number
    }

    class ViewHolder(private val binding: ItemSettingBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Setting, onItemClick: (Setting) -> Unit) {
            val name = item.name ?: ""
            val image = item.image
            val number = item.number

            with(binding) {
                this.name.text = name
                this.image.setImageResource(image)

                if (number != 0) {
                    this.number.visibility = View.VISIBLE
                    this.number.text = number.toString()
                } else {
                    this.number.visibility = View.GONE
                }

                this.item.setOnClickListener { onItemClick(item) }
            }
        }
    }
}
