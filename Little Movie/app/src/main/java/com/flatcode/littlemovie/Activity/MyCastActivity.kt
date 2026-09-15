package com.flatcode.littlemovie.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.Adapter.CastAdapter
import com.flatcode.littlemovie.Model.Cast
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.ViewModel.MyCastViewModel
import com.flatcode.littlemovie.databinding.ActivityMyCastBinding
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

class MyCastActivity : AppCompatActivity() {

    private var binding: ActivityMyCastBinding? = null
    private val activity: Activity = this@MyCastActivity
    private val viewModel: MyCastViewModel by viewModels()
    
    private val list = ArrayList<Cast?>()
    private lateinit var adapter: CastAdapter
    
    private var type: String = DATA.TIMESTAMP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCastBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setupUI()
        setupAdapter()
        observeViewModel()
    }

    private fun setupUI() {
        binding!!.toolbar.nameSpace.setText(R.string.my_cast)
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
                    adapter.filter.filter(s)
                } catch (e: Exception) {
                    Timber.e(e, "Error filtering cast")
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })

        binding!!.switchBar.explore.setOnClickListener { VOID.Intent1(activity, CLASS.CAST) }
        binding!!.switchBar.all.setOnClickListener {
            type = DATA.TIMESTAMP
            loadData()
        }
        binding!!.switchBar.mostMovies.setOnClickListener {
            type = DATA.MOVIES_COUNT
            loadData()
        }
        binding!!.switchBar.mostInterested.setOnClickListener {
            type = DATA.INTERESTED_COUNT
            loadData()
        }
        binding!!.switchBar.name.setOnClickListener {
            type = DATA.NAME
            loadData()
        }
    }

    private fun setupAdapter() {
        adapter = CastAdapter(activity, list)
        binding!!.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.cast.collect { castItems ->
                list.clear()
                list.addAll(castItems)
                adapter.notifyDataSetChanged()
                
                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", list.size)
                binding!!.progress.visibility = View.GONE
                if (list.isNotEmpty()) {
                    binding!!.recyclerView.visibility = View.VISIBLE
                    binding!!.emptyText.visibility = View.GONE
                } else {
                    binding!!.recyclerView.visibility = View.GONE
                    binding!!.emptyText.visibility = View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding!!.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    private fun loadData() {
        viewModel.loadCast(type)
    }

    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding!!.toolbar.toolbar.visibility = View.VISIBLE
            binding!!.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding!!.toolbar.textSearch.setText(DATA.EMPTY)
        } else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }
}
