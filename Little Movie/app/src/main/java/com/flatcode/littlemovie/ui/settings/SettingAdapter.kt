package com.flatcode.littlemovie.ui.settings

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.model.Setting
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.dialogAboutApp
import com.flatcode.littlemovie.utils.dialogLogout
import com.flatcode.littlemovie.utils.openActivity
import com.flatcode.littlemovie.utils.rateApp
import com.flatcode.littlemovie.utils.shareApp
import com.flatcode.littlemovie.databinding.ItemSettingBinding
import java.text.MessageFormat

class SettingAdapter(private val context: Context?, private val list: ArrayList<Setting>) :
    RecyclerView.Adapter<SettingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSettingBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]
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

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(val binding: ItemSettingBinding) : RecyclerView.ViewHolder(binding.root)
}
