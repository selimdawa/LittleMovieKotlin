package com.flatcode.littlemovieadmin.Adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovieadmin.Model.EditorsChoice
import com.flatcode.littlemovieadmin.Model.Movie
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.databinding.ItemMovieEditorsChoiceBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class EditorsChoiceAdapter(private val activity: Activity, var list: List<EditorsChoice>) :
    RecyclerView.Adapter<EditorsChoiceAdapter.ViewHolder>() {

    private var binding: ItemMovieEditorsChoiceBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding =
            ItemMovieEditorsChoiceBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding!!.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val id = position + 1
        val editorsChoiceId = DATA.EMPTY + id

        loadMovieDetails(
            id, editorsChoiceId, holder.name, holder.image, holder.nrViews,
            holder.nrLoves, holder.remove, holder.change, holder.addCard, holder.detailsCard
        )
        holder.numberEditorsChoice.text = MessageFormat.format("{0}{1}", DATA.EMPTY, id)

        holder.add.setOnClickListener {
            VOID.IntentExtra2(
                activity, CLASS.EDITORS_CHOICE_ADD,
                DATA.EDITORS_CHOICE_ID, editorsChoiceId, DATA.OLD_ID, null
            )
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        var add: ImageView
        var remove: ImageView
        var change: ImageView
        var image: ImageView
        var name: TextView
        var nrViews: TextView
        var nrLoves: TextView
        var numberEditorsChoice: TextView
        var item: LinearLayout
        var item2: LinearLayout
        var addCard: CardView
        var detailsCard: CardView

        init {
            nrLoves = binding!!.nrLoves
            nrViews = binding!!.nrViews
            name = binding!!.name
            image = binding!!.image
            item = binding!!.item
            item2 = binding!!.item2
            add = binding!!.add
            numberEditorsChoice = binding!!.numberEditorsChoice
            addCard = binding!!.addCard
            detailsCard = binding!!.detailsCard
            remove = binding!!.remove
            change = binding!!.change
        }
    }

    private fun loadMovieDetails(
        i: Int,
        position: String,
        title: TextView,
        image: ImageView,
        viewsCount: TextView,
        lovesCount: TextView,
        remove: ImageView,
        change: ImageView,
        addCard: CardView,
        detailsCard: CardView,
    ) {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (snapshot in dataSnapshot.children) {
                    val item = snapshot.getValue(Movie::class.java)!!
                    if (item.editorsChoice == i) {
                        val id = DATA.EMPTY + item.id
                        val name = DATA.EMPTY + item.name
                        loadData(id)
                        addCard.visibility = View.GONE
                        detailsCard.visibility = View.VISIBLE
                        remove.visibility = View.VISIBLE
                        change.visibility = View.VISIBLE
                        remove.setOnClickListener {
                            VOID.dialogOptionDelete(
                                activity, id, name, DATA.EDITORS_CHOICE, DATA.EDITORS_CHOICE,
                                true, DATA.NULL, DATA.NULL, DATA.NULL, false, false,
                            )
                        }
                        change.setOnClickListener {
                            VOID.IntentExtra2(
                                activity, CLASS.EDITORS_CHOICE_ADD,
                                DATA.EDITORS_CHOICE_ID, position, DATA.OLD_ID, id
                            )
                        }
                    } else {
                        addCard.visibility = View.VISIBLE
                        detailsCard.visibility = View.GONE
                        remove.visibility = View.GONE
                        change.visibility = View.GONE
                    }
                }
            }

            private fun loadData(id: String) {
                val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
                ref.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        //get data
                        val item = dataSnapshot.getValue(Movie::class.java)!!
                        val name = DATA.EMPTY + item.name
                        val imageLink = DATA.EMPTY + item.image
                        val ViewsCount = DATA.EMPTY + item.viewsCount
                        val LovesCount = DATA.EMPTY + item.lovesCount

                        VOID.GlideImage(false, activity, imageLink, image)
                        title.text = name
                        viewsCount.text = ViewsCount
                        lovesCount.text = LovesCount
                        addCard.visibility = View.GONE
                        detailsCard.visibility = View.VISIBLE
                        remove.visibility = View.VISIBLE
                        change.visibility = View.VISIBLE
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }
}