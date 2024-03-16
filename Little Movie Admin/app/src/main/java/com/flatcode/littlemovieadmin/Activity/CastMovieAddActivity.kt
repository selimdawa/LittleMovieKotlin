package com.flatcode.littlemovieadmin.Activity

import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovieadmin.Adapter.CastMovieAddAdapter
import com.flatcode.littlemovieadmin.Adapter.CastMovieAddAdapter.Companion.castAddRemove
import com.flatcode.littlemovieadmin.Model.Cast
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.DATA.castMovie
import com.flatcode.littlemovieadmin.Unitimport.THEME
import com.flatcode.littlemovieadmin.databinding.ActivityCastMovieBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class CastMovieAddActivity : AppCompatActivity() {

    private var binding: ActivityCastMovieBinding? = null
    var activity: Activity = this@CastMovieAddActivity
    var list: ArrayList<Cast?>? = null
    var adapter: CastMovieAddAdapter? = null
    var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityCastMovieBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.toolbar.nameSpace.setText(R.string.add_cast)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        type = DATA.TIMESTAMP

        //binding.recyclerView.setHasFixedSize(true);
        list = ArrayList()
        adapter = CastMovieAddAdapter(activity, list!!)
        binding!!.recyclerView.adapter = adapter
    }

    private val data: Unit
        get() {
            val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.CAST)
            ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    list!!.clear()
                    for (data in dataSnapshot.children) {
                        val item = data.getValue(Cast::class.java)!!
                        list!!.add(item)
                    }
                    //for (i in 0..9) {
                    //    val cast = Cast(DATA.EMPTY + i, DATA.EMPTY + i, "basic")
                    //    list!!.add(cast)
                    //}
                    list!!.reverse()
                    adapter!!.notifyDataSetChanged()
                    binding!!.progress.visibility = View.GONE
                    if (list!!.isNotEmpty()) {
                        binding!!.recyclerView.visibility = View.VISIBLE
                        binding!!.emptyText.visibility = View.GONE
                    } else {
                        binding!!.recyclerView.visibility = View.GONE
                        binding!!.emptyText.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }

    override fun onRestart() {
        data
        super.onRestart()
    }

    override fun onResume() {
        data
        super.onResume()
    }

    override fun onBackPressed() {
        castMovie.clear()
        castMovie = castAddRemove as ArrayList<String?>
        super.onBackPressed()
    }
}