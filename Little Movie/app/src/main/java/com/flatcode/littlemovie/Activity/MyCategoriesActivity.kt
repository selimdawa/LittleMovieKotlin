package com.flatcode.littlemovie.Activity

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.Adapter.CategoryAdapter
import com.flatcode.littlemovie.Model.Category
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.ViewModel.MyCategoriesViewModel
import com.flatcode.littlemovie.databinding.ActivityMyCategoriesBinding
import kotlinx.coroutines.launch
import timber.log.Timber
import dagger.hilt.android.AndroidEntryPoint
import java.text.MessageFormat

@AndroidEntryPoint
class MyCategoriesActivity : AppCompatActivity() {

    private var binding: ActivityMyCategoriesBinding? = null
    private val activity: Activity = this@MyCategoriesActivity
    private val viewModel: MyCategoriesViewModel by viewModels()
    
    private val list = ArrayList<Category?>()
    private lateinit var adapter: CategoryAdapter
    
    private var type: String = DATA.TIMESTAMP

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivityMyCategoriesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding!!.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.updatePadding(left = systemBars.left, right = systemBars.right, bottom = systemBars.bottom)
            binding!!.toolbar.root.updatePadding(top = systemBars.top)
            insets
        }

        setupUI()
        setupAdapter()
        observeViewModel()
    }

    private fun setupUI() {
        binding!!.toolbar.nameSpace.setText(R.string.my_categories)
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
                    Timber.e(e, "Error filtering categories")
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })

        binding!!.switchBar.explore.setOnClickListener { VOID.Intent1(activity, CLASS.CATEGORIES) }
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
        adapter = CategoryAdapter(activity, list)
        binding!!.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                list.clear()
                list.addAll(categories)
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
        viewModel.loadCategories(type)
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
