package com.flatcode.littlemovie.ui.movie

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.databinding.ActivityShowMoreBinding
import com.flatcode.littlemovie.utils.BaseActivity
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class ShowMoreActivity : BaseActivity() {

    private var binding: ActivityShowMoreBinding? = null
    private val activity: Activity = this@ShowMoreActivity
    private val viewModel: MovieListViewModel by viewModels()

    private lateinit var adapter: MovieAdapter

    private var type: String? = null
    private var name: String? = null
    private var isReverse: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowMoreBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        type = intent.getStringExtra(DATA.SHOW_MORE_TYPE)
        name = intent.getStringExtra(DATA.SHOW_MORE_NAME)
        isReverse = intent.getStringExtra(DATA.SHOW_MORE_BOOLEAN)

        setupUI()
        setupAdapter()
        observeViewModel()
    }

    private fun setupUI() {
        binding!!.toolbar.nameSpace.text = name
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
                    Timber.e(e, "Error filtering movies")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    private fun setupAdapter() {
        adapter = MovieAdapter(true) { movie ->
            activity.openActivity<MovieDetailsActivity>(
                DATA.MOVIE_ID to movie.id, DATA.MOVIE_LINK to movie.movieLink
            )
        }
        binding!!.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.movies.collect { movies ->
                adapter.setFullList(movies)

                binding!!.progress.visibility = View.GONE
                if (movies.isNotEmpty()) {
                    binding!!.recyclerView.visibility = View.VISIBLE
                    binding!!.emptyText.visibility = View.GONE
                } else {
                    binding!!.recyclerView.visibility = View.GONE
                    binding!!.emptyText.visibility = View.VISIBLE
                }
            }
        }

        lifecycleScope.launch {
            viewModel.moviesCount.collect { count ->
                binding!!.toolbar.number.text = MessageFormat.format("( {0} )", count)
            }
        }

        lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding!!.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        type?.let { viewModel.loadMovies(it, isReverse == "true") }
    }
}