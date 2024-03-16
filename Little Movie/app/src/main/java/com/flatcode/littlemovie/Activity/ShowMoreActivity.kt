package com.flatcode.littlemovie.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovie.Adapter.MovieAdapter
import com.flatcode.littlemovie.Model.Movie
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.THEME
import com.flatcode.littlemovie.databinding.ActivityShowMoreBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class ShowMoreActivity : AppCompatActivity() {

    private var binding: ActivityShowMoreBinding? = null
    var activity: Activity = this@ShowMoreActivity
    var list: ArrayList<Movie?>? = null
    var adapter: MovieAdapter? = null
    var type: String? = null
    var name: String? = null
    var isReverse: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityShowMoreBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        val intent = intent
        type = intent.getStringExtra(DATA.SHOW_MORE_TYPE)
        name = intent.getStringExtra(DATA.SHOW_MORE_NAME)
        isReverse = intent.getStringExtra(DATA.SHOW_MORE_BOOLEAN)

        binding!!.toolbar.nameSpace.text = name
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

        //binding.recyclerView.setHasFixedSize(true);
        list = ArrayList()
        binding!!.recyclerView.adapter = adapter
        adapter = MovieAdapter(activity, list!!, true)
    }

    private fun getData(orderBy: String?) {
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list!!.clear()
                var i = 0
                for (data in dataSnapshot.children) {
                    val item = data.getValue(Movie::class.java)!!
                    if (orderBy == DATA.EDITORS_CHOICE) {
                        if (item.editorsChoice > 0) {
                            list!!.add(item)
                            i++
                        }
                    } else {
                        list!!.add(item)
                        i++
                    }
                    if (isReverse == "true") list!!.reverse()
                    binding!!.toolbar.number.text = MessageFormat.format("( {0} )", i)
                    binding!!.recyclerView.adapter = adapter
                }
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