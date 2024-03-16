package com.flatcode.littlemovie.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.flatcode.littlemovie.Adapter.MovieAdapter
import com.flatcode.littlemovie.Model.Movie
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.databinding.FragmentMyMoviesBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class myMoviesFragment : Fragment() {

    private var binding: FragmentMyMoviesBinding? = null
    var check: MutableList<String?>? = null
    var list: ArrayList<Movie?>? = null
    var adapter: MovieAdapter? = null
    private var type: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMyMoviesBinding.inflate(LayoutInflater.from(context), container, false)

        type = DATA.TIMESTAMP
        //binding.recyclerCategory.setHasFixedSize(true);
        list = ArrayList()
        binding!!.recyclerView.adapter = adapter
        adapter = MovieAdapter(context, list!!, true)

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
        return binding!!.root
    }

    private fun getData(orderBy: String?) {
        check = ArrayList()
        val reference = FirebaseDatabase.getInstance().getReference(DATA.INTERESTED)
            .child(DATA.FirebaseUserUid)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (check as ArrayList<String?>).clear()
                for (snapshot in dataSnapshot.child(DATA.CATEGORIES).children)
                    (check as ArrayList<String?>).add(snapshot.key)
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
                for (snapshot in dataSnapshot.children) {
                    val song = snapshot.getValue(Movie::class.java)
                    for (id in check!!) {
                        assert(song != null)
                        if (song!!.id != null) if (song.categoryId == id) list!!.add(song)
                    }
                }
                list!!.reverse()
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

    override fun onStart() {
        getData(type)
        super.onStart()
    }
}