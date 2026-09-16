package com.flatcode.littlemovie.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import coil3.load
import com.flatcode.littlemovie.Adapter.ImageSliderAdapter.SliderViewHolder
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.smarteist.autoimageslider.SliderViewAdapter
import java.util.Objects

class ImageSliderAdapter(var context: Context?, var setTotalCount: Int) :
    SliderViewAdapter<SliderViewHolder>() {

    var ImageLink: String? = null

    override fun onCreateViewHolder(parent: ViewGroup): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slider, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: SliderViewHolder, position: Int) {
        FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val key = (position + 1).toString()
                    if (snapshot.hasChild(key)) {
                        ImageLink = snapshot.child(key).value?.toString()
                        ImageLink?.let {
                            viewHolder.ImageSlider.load(it)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun getCount(): Int {
        return setTotalCount
    }

    class SliderViewHolder(var itemView: View) : ViewHolder(itemView) {
        var ImageSlider: ImageView

        init {
            ImageSlider = itemView.findViewById(R.id.imageView)
        }
    }
}