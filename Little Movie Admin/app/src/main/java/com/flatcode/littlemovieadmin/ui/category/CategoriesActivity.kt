package com.flatcode.littlemovieadmin.ui.category

import com.flatcode.littlemovieadmin.utils.BaseActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ActivityCategoriesBinding
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.moreDeleteCategory
import com.flatcode.littlemovieadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.MessageFormat

@AndroidEntryPoint
class CategoriesActivity : BaseActivity() {

    private lateinit var binding: ActivityCategoriesBinding
    private val viewModel: CategoriesViewModel by viewModels()
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.categories)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        binding.toolbar.close.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                if (DATA.searchStatus) {
                    binding.toolbar.root.getChildAt(0).visibility = View.VISIBLE
                    binding.toolbar.root.getChildAt(1).visibility = View.GONE
                    DATA.searchStatus = false
                    binding.toolbar.textSearch.setText(DATA.EMPTY)
                    viewModel.setSearchQuery("")
                } else {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                }
            }
        })

        binding.toolbar.search.setOnClickListener {
            binding.toolbar.root.getChildAt(0).visibility = View.GONE
            binding.toolbar.root.getChildAt(1).visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s.toString())
            }

            override fun afterTextChanged(s: Editable) {}
        })

        adapter = CategoryAdapter(
            onMoreClick = { category ->
                moreDeleteCategory(
                    category,
                    DATA.NULL,
                    DATA.NULL,
                    DATA.NULL,
                    cast = false,
                    movie = false
                )
            },
            onItemClick = { category ->
                openActivity<CategoryDetailsActivity>(
                    extras = arrayOf(
                        DATA.CATEGORY_ID to category.id,
                        DATA.CATEGORY_NAME to category.name
                    )
                )
            }
        )
        binding.recyclerView.adapter = adapter

        binding.switchBar.all.setOnClickListener { viewModel.getData(DATA.TIMESTAMP) }
        binding.switchBar.mostMovies.setOnClickListener { viewModel.getData(DATA.MOVIES_COUNT) }
        binding.switchBar.mostInterested.setOnClickListener { viewModel.getData(DATA.INTERESTED_COUNT) }
        binding.switchBar.name.setOnClickListener { viewModel.getData(DATA.NAME) }

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(viewModel.categories, viewModel.isLoading) { list, isLoading ->
                    Pair(list, isLoading)
                }.collect { (list, isLoading) ->
                    binding.toolbar.number.text = MessageFormat.format("( {0} )", list.size)
                    adapter.submitList(list)

                    binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE

                    if (isLoading) {
                        binding.emptyText.visibility = View.GONE
                        binding.recyclerView.visibility = if (list.isNotEmpty()) View.VISIBLE else View.GONE
                    } else {
                        if (list.isNotEmpty()) {
                            binding.recyclerView.visibility = View.VISIBLE
                            binding.emptyText.visibility = View.GONE
                        } else {
                            binding.recyclerView.visibility = View.GONE
                            binding.emptyText.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
    }
}
