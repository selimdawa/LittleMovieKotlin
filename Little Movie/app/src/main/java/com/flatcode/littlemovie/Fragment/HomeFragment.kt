package com.flatcode.littlemovie.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.flatcode.littlemovie.Adapter.CategoryHomeAdapter
import com.flatcode.littlemovie.Adapter.ImageSliderAdapter
import com.flatcode.littlemovie.Adapter.MovieAdapter
import com.flatcode.littlemovie.Model.Category
import com.flatcode.littlemovie.Model.Movie
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.databinding.FragmentHomeBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener

class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private var list: ArrayList<Movie?>? = null
    private var list2: ArrayList<Movie?>? = null
    private var list3: ArrayList<Movie?>? = null
    private var list4: ArrayList<Movie?>? = null
    private var adapter: MovieAdapter? = null
    private var adapter2: MovieAdapter? = null
    private var adapter3: MovieAdapter? = null
    private var adapter4: MovieAdapter? = null
    private val B_one = false
    private val B_two = true
    private val B_three = true
    private val B_four = true
    var TotalCounts = 0
    private var categoryList: ArrayList<Category?>? = null
    private var categoryAdapter: CategoryHomeAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(LayoutInflater.from(context), container, false)

        binding!!.showMore.setOnClickListener {
            VOID.IntentExtra3(
                context, CLASS.SHOW_MORE, DATA.SHOW_MORE_TYPE,
                DATA.EDITORS_CHOICE, DATA.SHOW_MORE_NAME, binding!!.name.text.toString(),
                DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + B_one
            )
        }
        binding!!.showMore2.setOnClickListener {
            VOID.IntentExtra3(
                context, CLASS.SHOW_MORE, DATA.SHOW_MORE_TYPE,
                DATA.VIEWS_COUNT, DATA.SHOW_MORE_NAME, binding!!.mostViews.text.toString(),
                DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + B_two
            )
        }
        binding!!.showMore3.setOnClickListener {
            VOID.IntentExtra3(
                context, CLASS.SHOW_MORE, DATA.SHOW_MORE_TYPE,
                DATA.LOVES_COUNT, DATA.SHOW_MORE_NAME, binding!!.name3.text.toString(),
                DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + B_three
            )
        }
        binding!!.showMore4.setOnClickListener {
            VOID.IntentExtra3(
                context, CLASS.SHOW_MORE, DATA.SHOW_MORE_TYPE,
                DATA.TIMESTAMP, DATA.SHOW_MORE_NAME, binding!!.name4.text.toString(),
                DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + B_four
            )
        }

        //RecyclerView Category
        //binding.recyclerCategory.setHasFixedSize(true);
        categoryList = ArrayList()
        categoryAdapter = CategoryHomeAdapter(context, categoryList!!)
        binding!!.recyclerCategory.adapter = categoryAdapter

        //RecyclerView Editor's Choice
        //binding.recyclerView.setHasFixedSize(true);
        list = ArrayList()
        adapter = MovieAdapter(context, list!!, false)
        binding!!.recyclerView.adapter = adapter

        //RecyclerView Views Count
        //binding.recyclerView2.setHasFixedSize(true);
        list2 = ArrayList()
        adapter2 = MovieAdapter(context, list2!!, false)
        binding!!.recyclerView2.adapter = adapter2

        //RecyclerView Loves Count
        //binding.recyclerView3.setHasFixedSize(true);
        list3 = ArrayList()
        adapter3 = MovieAdapter(context, list3!!, false)
        binding!!.recyclerView3.adapter = adapter3

        //RecyclerView New Songs
        //binding.recyclerView4.setHasFixedSize(true);
        list4 = ArrayList()
        adapter4 = MovieAdapter(context, list4!!, false)
        binding!!.recyclerView4.adapter = adapter4

        FirebaseDatabase.getInstance().getReference(DATA.SLIDER_SHOW)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val counts = snapshot.childrenCount
                    TotalCounts = counts.toInt()
                    binding!!.imageSlider.sliderAdapter = ImageSliderAdapter(context, TotalCounts)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
        return binding!!.root
    }

    private fun init() {
        loadCategories()
        loadPostEditorsChoice(
            DATA.EDITORS_CHOICE, list, adapter, binding!!.bar,
            binding!!.recyclerView, binding!!.empty
        )
        loadPostBy(
            DATA.VIEWS_COUNT, list2, adapter2, binding!!.bar2,
            binding!!.recyclerView2, binding!!.empty2
        )
        loadPostBy(
            DATA.LOVES_COUNT, list3, adapter3, binding!!.bar3,
            binding!!.recyclerView3, binding!!.empty3
        )
        loadPostBy(
            DATA.TIMESTAMP, list4, adapter4, binding!!.bar4,
            binding!!.recyclerView4, binding!!.empty4
        )
    }

    private fun loadCategories() {
        val ref = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList!!.clear()
                for (data in snapshot.children) {
                    val category = data.getValue(Category::class.java)
                    categoryList!!.add(category)
                }
                categoryAdapter!!.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun loadPostBy(
        orderBy: String?, list: ArrayList<Movie?>?, adapter: MovieAdapter?,
        bar: ProgressBar, recyclerView: RecyclerView, empty: TextView
    ) {
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        ref.orderByChild(orderBy!!).limitToLast(DATA.ORDER_MAIN)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    list!!.clear()
                    for (data in snapshot.children) {
                        val item = data.getValue(Movie::class.java)!!
                        if (orderBy != DATA.EDITORS_CHOICE) list.add(item)
                    }
                    adapter!!.notifyDataSetChanged()
                    bar.visibility = View.GONE
                    if (list.isNotEmpty()) {
                        recyclerView.visibility = View.VISIBLE
                        empty.visibility = View.GONE
                        if (orderBy != DATA.EDITORS_CHOICE) list.reverse()
                    } else {
                        recyclerView.visibility = View.GONE
                        empty.visibility = View.VISIBLE
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadPostEditorsChoice(
        orderBy: String?, list: ArrayList<Movie?>?, adapter: MovieAdapter?,
        bar: ProgressBar, recyclerView: RecyclerView, empty: TextView
    ) {
        val ref: Query = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
        ref.orderByChild(orderBy!!).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                list!!.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(Movie::class.java)!!
                    if (orderBy == DATA.EDITORS_CHOICE) {
                        if (item.editorsChoice <= 2 && item.editorsChoice > 0) list.add(item)
                    }
                }
                adapter!!.notifyDataSetChanged()
                bar.visibility = View.GONE
                if (list.isNotEmpty()) {
                    recyclerView.visibility = View.VISIBLE
                    empty.visibility = View.GONE
                    if (orderBy != DATA.EDITORS_CHOICE) list.reverse()
                } else {
                    recyclerView.visibility = View.GONE
                    empty.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun onStart() {
        init()
        super.onStart()
    }
}