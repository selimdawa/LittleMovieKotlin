package com.flatcode.littlemovieadmin.ui.movie

import com.flatcode.littlemovieadmin.utils.BaseActivity
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ActivityCastMovieBinding
import com.flatcode.littlemovieadmin.utils.DATA.castMovie
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CastMovieAddActivity : BaseActivity() {

    private lateinit var binding: ActivityCastMovieBinding
    private val viewModel: CastMovieAddViewModel by viewModels()
    private lateinit var adapter: CastMovieAddAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCastMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.add_cast)
        binding.toolbar.back.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(enabled = true) {
            override fun handleOnBackPressed() {
                castMovie.clear()
                castMovie.addAll(adapter.selectedIds)
                isEnabled = false
                onBackPressedDispatcher.onBackPressed()
                isEnabled = true
            }
        })

        adapter = CastMovieAddAdapter(castMovie)
        binding.recyclerView.adapter = adapter

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                combine(viewModel.castList, viewModel.isLoading) { list, isLoading ->
                    Pair(list, isLoading)
                }.collect { (list, isLoading) ->
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
