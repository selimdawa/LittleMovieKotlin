package com.flatcode.littlemovieadmin.ui.favorite

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.ui.movie.MovieAdapter
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.ui.favorite.FavoritesViewModel
import com.flatcode.littlemovieadmin.databinding.ActivityMoviesBinding
import com.flatcode.littlemovieadmin.ui.movie.MovieDetailsActivity
import com.flatcode.littlemovieadmin.utils.checkFavorite
import com.flatcode.littlemovieadmin.utils.moreDeleteMovie
import com.flatcode.littlemovieadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class FavoritesActivity : BaseActivity() {

    private lateinit var binding: ActivityMoviesBinding
    private val viewModel: FavoritesViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoviesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.nameSpace.setText(R.string.favorites)
        binding.toolbar.close.setOnClickListener { onBackPressed() }
        binding.toolbar.back.setOnClickListener { onBackPressed() }

        binding.toolbar.search.setOnClickListener {
            binding.toolbar.toolbar.visibility = View.GONE
            binding.toolbar.toolbarSearch.visibility = View.VISIBLE
            DATA.searchStatus = true
        }

        binding.toolbar.textSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    adapter.filter(s.toString())
                } catch (e: Exception) {
                    Timber.e(e, "Filter error")
                }
            }
            override fun afterTextChanged(s: Editable) {}
        })

        adapter = MovieAdapter(
            onItemClick = { movie ->
                openActivity<MovieDetailsActivity>(
                    extras = arrayOf(
                        DATA.MOVIE_ID to movie.id,
                        DATA.MOVIE_LINK to movie.movieLink
                    )
                )
            },
            onMoreClick = { movie ->
                moreDeleteMovie(
                    movie, DATA.CATEGORIES, movie.categoryId ?: DATA.EMPTY, DATA.MOVIES_COUNT, false, true
                )
            },
            onFavoriteClick = { movie, imageView ->
                imageView.checkFavorite(movie.id)
            }
        )
        binding.recyclerView.adapter = adapter

        binding.switchBar.all.setOnClickListener { viewModel.getData(DATA.TIMESTAMP) }
        binding.switchBar.mostViews.setOnClickListener { viewModel.getData(DATA.VIEWS_COUNT) }
        binding.switchBar.mostLoves.setOnClickListener { viewModel.getData(DATA.LOVES_COUNT) }
        binding.switchBar.name.setOnClickListener { viewModel.getData(DATA.NAME) }

        observeState()
    }

    private fun observeState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    binding.progress.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.toolbar.number.text = MessageFormat.format("( {0} )", state.count)
                    
                    adapter.list = state.movies
                    adapter.submitList(state.movies)

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
        } else if (DATA.isChange) {
            onResume()
            DATA.isChange = false
        } else super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
        viewModel.getData(viewModel.uiState.value.currentType)
    }
}
