package com.flatcode.littlemovieadmin.Activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.Adapter.EditorsChoiceMovieAdapter
import com.flatcode.littlemovieadmin.Model.Movie
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Unit.DATA
import com.flatcode.littlemovieadmin.ViewModel.EditorsChoiceAddViewModel
import com.flatcode.littlemovieadmin.databinding.ActivityEditorsChoiceAddBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class EditorsChoiceAddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditorsChoiceAddBinding
    private val viewModel: EditorsChoiceAddViewModel by viewModels()
    private val list = mutableListOf<Movie?>()
    private lateinit var adapter: EditorsChoiceMovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditorsChoiceAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val editorsChoiceId = intent.getStringExtra(DATA.EDITORS_CHOICE_ID)
        val oldId = intent.getStringExtra(DATA.OLD_ID)
        val id = editorsChoiceId?.toInt() ?: 0

        viewModel.init(editorsChoiceId, oldId)

        binding.toolbar.nameSpace.setText(R.string.editors_choice)
        binding.toolbar.back.setOnClickListener { onBackPressed() }
        binding.toolbar.close.setOnClickListener { onBackPressed() }

        binding.toolbar.search.setOnClickListener {
            binding.toolbar.toolbar.visibility = View.GONE
            binding.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter.filter.filter(s)
                } catch (e: Exception) {
                    Timber.e(e, "Filter error")
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })

        adapter = EditorsChoiceMovieAdapter(this, oldId, list as ArrayList<Movie?>, id)
        binding.recyclerView.adapter = adapter

        binding.all.setOnClickListener { viewModel.getData(DATA.TIMESTAMP) }
        binding.name.setOnClickListener { viewModel.getData(DATA.NAME) }
        binding.mostViews.setOnClickListener { viewModel.getData(DATA.VIEWS_COUNT) }
        binding.mostLoves.setOnClickListener { viewModel.getData(DATA.LOVES_COUNT) }
        binding.favorites.setOnClickListener { viewModel.getFavorites(DATA.NAME) }

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.toolbar.number.text = MessageFormat.format("( {0} )", state.count)
                    
                    list.clear()
                    list.addAll(state.movies)
                    adapter.notifyDataSetChanged()

                    if (state.movies.isNotEmpty()) {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.emptyText.visibility = View.GONE
                    } else if (!state.isLoading) {
                        binding.recyclerView.visibility = View.GONE
                        binding.emptyText.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        if (DATA.searchStatus) {
            binding.toolbar.toolbar.visibility = View.VISIBLE
            binding.toolbar.toolbarSearch.visibility = View.GONE
            DATA.searchStatus = false
            binding.toolbar.textSearch.setText(DATA.EMPTY)
        } else super.onBackPressed()
    }
}
