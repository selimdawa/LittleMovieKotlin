package com.flatcode.littlemovieadmin.ui.cast

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.databinding.ActivityCastDetailsBinding
import com.flatcode.littlemovieadmin.ui.BaseActivity
import com.flatcode.littlemovieadmin.ui.movie.MovieAdapter
import com.flatcode.littlemovieadmin.ui.movie.MovieDetailsActivity
import com.flatcode.littlemovieadmin.utils.DATA
import com.flatcode.littlemovieadmin.utils.checkFavorite
import com.flatcode.littlemovieadmin.utils.dialogAboutArtist
import com.flatcode.littlemovieadmin.utils.loadBlur
import com.flatcode.littlemovieadmin.utils.loadImage
import com.flatcode.littlemovieadmin.utils.moreDeleteMovie
import com.flatcode.littlemovieadmin.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.MessageFormat

@AndroidEntryPoint
class CastDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityCastDetailsBinding
    private val viewModel: CastDetailsViewModel by viewModels()
    private lateinit var adapter: MovieAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCastDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val castId = intent.getStringExtra(DATA.CAST_ID) ?: ""
        val castName = intent.getStringExtra(DATA.CAST_NAME)
        val castImage = intent.getStringExtra(DATA.CAST_IMAGE)
        val castAbout = intent.getStringExtra(DATA.CAST_ABOUT)

        viewModel.init(castId, castName, castImage, castAbout)

        binding.toolbar.nameSpace.setText(R.string.cast_details)
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
                    adapter.filter(s.toString())
                } catch (e: Exception) {
                    Timber.e(e, "Filter error")
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.go.setOnClickListener {
            val state = viewModel.uiState.value
            dialogAboutArtist(state.castImage, state.castName, state.castAbout)
        }

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
                    movie,
                    DATA.CATEGORIES,
                    movie.categoryId ?: DATA.EMPTY,
                    DATA.MOVIES_COUNT,
                    false,
                    true
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
                    binding.name.text = state.castName

                    binding.image.loadImage(state.castImage, true)
                    binding.imageBlur.loadBlur(state.castImage, 50, true)

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
