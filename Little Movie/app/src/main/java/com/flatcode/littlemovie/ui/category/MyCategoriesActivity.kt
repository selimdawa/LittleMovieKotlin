package com.flatcode.littlemovie.ui.category

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.R
import com.flatcode.littlemovie.databinding.ActivityMyCategoriesBinding
import com.flatcode.littlemovie.utils.BaseActivity
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class MyCategoriesActivity : BaseActivity() {

    private var binding: ActivityMyCategoriesBinding? = null
    private val activity: Activity = this@MyCategoriesActivity
    private val viewModel: MyCategoriesViewModel by viewModels()

    private lateinit var adapter: CategoryAdapter

    private var type: String = DATA.TIMESTAMP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyCategoriesBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        setupUI()
        setupAdapter()
        observeViewModel()
    }

    private fun setupUI() {
        binding!!.toolbar.nameSpace.setText(R.string.my_categories)
        binding!!.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding!!.toolbar.close.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (DATA.searchStatus) {
                    binding!!.toolbar.toolbar.visibility = View.VISIBLE
                    binding!!.toolbar.toolbarSearch.visibility = View.GONE
                    DATA.searchStatus = false
                    binding!!.toolbar.textSearch.setText(DATA.EMPTY)
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                }
            }
        })

        binding!!.toolbar.search.setOnClickListener {
            binding!!.toolbar.toolbar.visibility = View.GONE
            binding!!.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding!!.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter.filter(s.toString())
                } catch (e: Exception) {
                    Timber.e(e, "Error filtering categories")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding!!.switchBar.explore.setOnClickListener { activity.openActivity<CategoriesActivity>() }
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
        adapter = CategoryAdapter { category ->
            activity.openActivity<CategoryDetailsActivity>(
                DATA.CATEGORY_ID to category.id, DATA.CATEGORY_NAME to category.name
            )
        }
        binding!!.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                adapter.setFullList(categories)

                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", categories.size)
                binding!!.progress.visibility = View.GONE
                if (categories.isNotEmpty()) {
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


    override fun onResume() {
        super.onResume()
        loadData()
    }
}