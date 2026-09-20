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
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.dialogAboutApp
import com.flatcode.littlemovie.utils.dialogLogout
import com.flatcode.littlemovie.utils.openActivity
import com.flatcode.littlemovie.utils.rateApp
import com.flatcode.littlemovie.utils.shareApp
import java.text.MessageFormat

class SettingAdapter(private val context: Context?) :
    ListAdapter<Setting, SettingAdapter.ViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSettingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        val id = DATA.EMPTY + item.id
        val name = DATA.EMPTY + item.name
        val image = item.image
        val number = item.number
        val to = item.c

        holder.binding.name.text = name
        holder.binding.image.setImageResource(image)

        if (number != 0) {
            holder.binding.number.visibility = View.VISIBLE
            holder.binding.number.text = MessageFormat.format("{0}{1}", DATA.EMPTY, number)
        } else {
            holder.binding.number.visibility = View.GONE
        }

        holder.binding.item.setOnClickListener {
            when (id) {
                "5" -> context?.dialogAboutApp()
                "6" -> context?.dialogLogout()
                "7" -> context?.shareApp()
                "8" -> context?.rateApp()
                else -> to?.let {
                    val intent = Intent(context, it)
                    context?.startActivity(intent)
                }
            }
        }
    }

    object DiffCallback : DiffUtil.ItemCallback<Setting>() {
        override fun areItemsTheSame(oldItem: Setting, newItem: Setting): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Setting, newItem: Setting): Boolean =
            oldItem.id == newItem.id && oldItem.name == newItem.name && oldItem.number == newItem.number
    }

    inner class ViewHolder(val binding: ItemSettingBinding) : RecyclerView.ViewHolder(binding.root)
}
