package com.flatcode.littlemovieadmin.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.Model.Cast
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.DATA.castMovie
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.databinding.ItemCastMovieAddBinding

class CastMovieAddAdapter(private val activity: Activity, var list: ArrayList<Cast?>?) :
    RecyclerView.Adapter<CastMovieAddAdapter.ViewHolder>() {

    private var binding: ItemCastMovieAddBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = ItemCastMovieAddBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list!![position]
        val id = DATA.EMPTY + item!!.id
        val name = DATA.EMPTY + item.name
        val image = DATA.EMPTY + item.image

        VOID.GlideImage(true, activity, image, holder.image)

        if (name == DATA.EMPTY) {
            holder.name.visibility = View.GONE
        } else {
            holder.name.visibility = View.VISIBLE
            holder.name.text = name
        }

        castAddRemove = castMovie
        checkRemove(id, holder.add, holder.remove)
        checkAdd(id, holder.add, holder.remove)

        holder.add.setOnClickListener {
            castAddRemove = castAddRemove as ArrayList<String?> + id
            checkRemove(id, holder.add, holder.remove)
            checkAdd(id, holder.add, holder.remove)
        }

        holder.remove.setOnClickListener {
            castAddRemove = castAddRemove as ArrayList<String?> - id
            checkRemove(id, holder.add, holder.remove)
            checkAdd(id, holder.add, holder.remove)
        }
    }

    override fun getItemCount(): Int {
        return list!!.size
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var image: ImageView
        var add: ImageView
        var remove: ImageView
        var name: TextView
        var item: LinearLayout

        init {
            image = binding!!.image
            name = binding!!.name
            add = binding!!.add
            remove = binding!!.remove
            item = binding!!.item
        }
    }

    private fun checkAdd(id: String, add: ImageView, remove: ImageView) {
        for (i in castAddRemove!!.indices) {
            if (castAddRemove!![i] == id) {
                add.visibility = View.GONE
                remove.visibility = View.VISIBLE
            }
        }
    }

    private fun checkRemove(id: String, add: ImageView, remove: ImageView) {
        for (i in castAddRemove!!.indices) {
            if (castAddRemove!![i] != id) {
                add.visibility = View.VISIBLE
                remove.visibility = View.GONE
            }
        }
    }

    companion object {
        var castAddRemove: List<String?>? = null
    }
}