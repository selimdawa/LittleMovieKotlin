package com.flatcode.littlemovieadmin.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovieadmin.Adapter.MovieAdapter
import com.flatcode.littlemovieadmin.Model.Movie
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.Unit.VOID
import com.flatcode.littlemovieadmin.Unitimport.THEME
import com.flatcode.littlemovieadmin.databinding.ActivityCastDetailsBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class CastDetailsActivity : AppCompatActivity() {

    private var binding: ActivityCastDetailsBinding? = null
    var activity: Activity = this@CastDetailsActivity
    var item: MutableList<String?>? = null
    var list: ArrayList<Movie?>? = null
    var adapter: MovieAdapter? = null
    var type: String? = null
    var castId: String? = null
    var castName: String? = null
    var castImage: String? = null
    var castAbout: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityCastDetailsBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        castId = intent.getStringExtra(DATA.CAST_ID)
        castName = intent.getStringExtra(DATA.CAST_NAME)
        castImage = intent.getStringExtra(DATA.CAST_IMAGE)
        castAbout = intent.getStringExtra(DATA.CAST_ABOUT)

        type = DATA.TIMESTAMP
        VOID.GlideImage(true, activity, castImage, binding!!.image)
        VOID.GlideBlur(true, activity, castImage, binding!!.imageBlur, 50)

        binding!!.toolbar.nameSpace.setText(R.string.cast_details)
        binding!!.name.text = castName
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.toolbar.close.setOnClickListener { onBackPressed() }

        binding!!.toolbar.search.setOnClickListener {
            binding!!.toolbar.toolbar.visibility = View.GONE
            binding!!.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding!!.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter!!.filter.filter(s)
                } catch (e: Exception) {
                    //None
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
        binding!!.go.setOnClickListener {
            VOID.dialogAboutArtist(activity, castImage, castName, castAbout)
        }

        //binding.recyclerView.setHasFixedSize(true);
        list = ArrayList()
        adapter = MovieAdapter(activity, list!!)
        binding!!.recyclerView.adapter = adapter

        binding!!.switchBar.all.setOnClickListener {
            type = DATA.TIMESTAMP
            getData(type)
        }
        binding!!.switchBar.mostViews.setOnClickListener {
            type = DATA.VIEWS_COUNT
            getData(type)
        }
        binding!!.switchBar.mostLoves.setOnClickListener {
            type = DATA.LOVES_COUNT
            getData(type)
        }
        binding!!.switchBar.name.setOnClickListener {
            type = DATA.NAME
            getData(type)
        }
    }

    private fun getData(orderBy: String?) {
        item = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference(DATA.CAST_MOVIE)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (item as ArrayList<String?>).clear()
                for (snapshot in dataSnapshot.children) {
                    for (snapshot2 in snapshot.children) {
                        if (snapshot2.key == castId) (item as ArrayList<String?>).add(snapshot.key)
                    }
                }
                getItems(orderBy)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getItems(orderBy: String?) {
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list!!.clear()
                var i = 0
                for (snapshot in dataSnapshot.children) {
                    val movie = snapshot.getValue(Movie::class.java)
                    for (id in item!!) {
                        assert(movie != null)
                        if (movie!!.id != null) if (movie.id == id) {
                            list!!.add(movie)
                            i++
                        }
                    }
                }
                list!!.reverse()
                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", i)
                binding!!.recyclerView.adapter = adapter
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

    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding!!.toolbar.toolbar.visibility = View.VISIBLE
            binding!!.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding!!.toolbar.textSearch.setText(DATA.EMPTY)
        } else if (DATA.isChange) {
            onResume()
            DATA.isChange = false
        } else super.onBackPressed()
    }

    override fun onRestart() {
        getData(type)
        super.onRestart()
    }

    override fun onResume() {
        getData(type)
        super.onResume()
    }
}