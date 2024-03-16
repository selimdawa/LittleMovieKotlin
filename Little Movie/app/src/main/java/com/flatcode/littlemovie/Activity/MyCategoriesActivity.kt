package com.flatcode.littlemovie.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovie.Adapter.CategoryAdapter
import com.flatcode.littlemovie.Model.Category
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.THEME
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.databinding.ActivityMyCategoriesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import java.text.MessageFormat

class MyCategoriesActivity : AppCompatActivity() {

    private var binding: ActivityMyCategoriesBinding? = null
    var activity: Activity = this@MyCategoriesActivity
    var item: MutableList<String?>? = null
    var list: ArrayList<Category?>? = null
    var adapter: CategoryAdapter? = null
    var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityMyCategoriesBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.toolbar.nameSpace.setText(R.string.my_categories)
        binding!!.toolbar.back.setOnClickListener { onBackPressed() }
        binding!!.toolbar.close.setOnClickListener { onBackPressed() }
        type = DATA.TIMESTAMP

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
        adapter = CategoryAdapter(activity, list!!)
        binding!!.recyclerView.adapter = adapter

        binding!!.switchBar.explore.setOnClickListener { VOID.Intent1(activity, CLASS.CATEGORIES) }
        binding!!.switchBar.all.setOnClickListener {
            type = DATA.TIMESTAMP
            getData(type)
        }
        binding!!.switchBar.mostMovies.setOnClickListener {
            type = DATA.MOVIES_COUNT
            getData(type)
        }
        binding!!.switchBar.mostInterested.setOnClickListener {
            type = DATA.INTERESTED_COUNT
            getData(type)
        }
        binding!!.switchBar.name.setOnClickListener {
            type = DATA.NAME
            getData(type)
        }
    }

    private fun getData(orderBy: String?) {
        item = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
            .child(DATA.FirebaseUserUid).child(DATA.CATEGORIES)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (item as ArrayList<String?>).clear()
                for (snapshot in dataSnapshot.children) {
                    (item as ArrayList<String?>).add(snapshot.key)
                }
                getItems(orderBy)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun getItems(orderBy: String?) {
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list!!.clear()
                var i = 0
                for (data in dataSnapshot.children) {
                    val category = data.getValue(Category::class.java)
                    for (id in item!!) {
                        assert(category != null)
                        if (category!!.id != null) if (category.id == id) {
                            list!!.add(category)
                            i++
                        }
                    }
                }
                list!!.reverse()
                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", i)
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