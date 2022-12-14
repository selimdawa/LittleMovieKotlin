package com.flatcode.littlemovie.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.flatcode.littlemovie.Adapter.CastAdapter
import com.flatcode.littlemovie.Model.Cast
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.THEME
import com.flatcode.littlemovie.databinding.ActivityCastBinding
import com.google.firebase.database.*
import java.text.MessageFormat
import java.util.*

class CastActivity : AppCompatActivity() {

    private var binding: ActivityCastBinding? = null
    var activity: Activity = this@CastActivity
    var list: ArrayList<Cast?>? = null
    var adapter: CastAdapter? = null
    var type: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        THEME.setThemeOfApp(activity)
        super.onCreate(savedInstanceState)
        binding = ActivityCastBinding.inflate(layoutInflater)
        val view = binding!!.root
        setContentView(view)

        binding!!.toolbar.nameSpace.setText(R.string.cast)
        binding!!.toolbar.back.setOnClickListener { v: View? -> onBackPressed() }
        type = DATA.TIMESTAMP
        binding!!.toolbar.search.setOnClickListener { v: View? ->
            binding!!.toolbar.toolbar.visibility = View.GONE
            binding!!.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }
        binding!!.toolbar.close.setOnClickListener { v: View? -> onBackPressed() }
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
        adapter = CastAdapter(activity, list!!)
        binding!!.recyclerView.adapter = adapter
        binding!!.switchBar.all.setOnClickListener { v: View? ->
            type = DATA.TIMESTAMP
            getData(type)
        }
        binding!!.switchBar.mostMovies.setOnClickListener { v: View? ->
            type = DATA.MOVIES_COUNT
            getData(type)
        }
        binding!!.switchBar.mostInterested.setOnClickListener { v: View? ->
            type = DATA.INTERESTED_COUNT
            getData(type)
        }
        binding!!.switchBar.name.setOnClickListener { v: View? ->
            type = DATA.NAME
            getData(type)
        }
    }

    private fun getData(orderBy: String?) {
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.CAST)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                list!!.clear()
                var i = 0
                for (data in dataSnapshot.children) {
                    val item = data.getValue(Cast::class.java)!!
                    list!!.add(item)
                    i++
                }
                Collections.reverse(list)
                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", i)
                adapter!!.notifyDataSetChanged()
                binding!!.progress.visibility = View.GONE
                if (!list!!.isEmpty()) {
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