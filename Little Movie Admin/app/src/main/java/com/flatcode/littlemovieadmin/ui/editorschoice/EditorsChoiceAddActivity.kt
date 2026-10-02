package com.flatcode.littlemovieadmin.ui.editorschoice

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
import com.flatcode.littlemovieadmin.databinding.ActivityEditorsChoiceAddBinding
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.addToEditorsChoice
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.MessageFormat

@AndroidEntryPoint
class EditorsChoiceAddActivity : BaseActivity() {

    private lateinit var binding: ActivityEditorsChoiceAddBinding
    private val viewModel: EditorsChoiceAddViewModel by viewModels()
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

        adapter = EditorsChoiceMovieAdapter(
            onAddClick = { movie ->
                if (oldId != null) {
                    addToEditorsChoice(this, movie.id, id)
                    addToEditorsChoice(this, oldId, 0)
                } else {
                    addToEditorsChoice(this, movie.id, id)
                }
            }
        )
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
                combine(viewModel.movies, viewModel.isLoading) { list, isLoading ->
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
