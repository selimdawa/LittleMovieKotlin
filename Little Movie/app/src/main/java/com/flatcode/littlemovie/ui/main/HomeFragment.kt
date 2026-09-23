package com.flatcode.littlemovie.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.databinding.FragmentHomeBinding
import com.flatcode.littlemovie.model.Movie
import com.flatcode.littlemovie.ui.category.CategoryDetailsActivity
import com.flatcode.littlemovie.ui.category.CategoryHomeAdapter
import com.flatcode.littlemovie.ui.movie.MovieAdapter
import com.flatcode.littlemovie.ui.movie.MovieDetailsActivity
import com.flatcode.littlemovie.ui.movie.ShowMoreActivity
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.utils.openActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()

    private lateinit var categoryAdapter: CategoryHomeAdapter
    private lateinit var editorsChoiceAdapter: MovieAdapter
    private lateinit var mostViewedAdapter: MovieAdapter
    private lateinit var mostLovedAdapter: MovieAdapter
    private lateinit var newMoviesAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupClickListeners()
        setupAdapters()
        observeViewModel()

        return binding.root
    }

    private fun setupClickListeners() {
        binding.showMore.setOnClickListener {
            context?.openActivity<ShowMoreActivity>(
                DATA.SHOW_MORE_TYPE to DATA.EDITORS_CHOICE,
                DATA.SHOW_MORE_NAME to binding.name.text.toString(),
                DATA.SHOW_MORE_BOOLEAN to (DATA.EMPTY + false)
            )
        }
        binding.showMore2.setOnClickListener {
            context?.openActivity<ShowMoreActivity>(
                DATA.SHOW_MORE_TYPE to DATA.VIEWS_COUNT,
                DATA.SHOW_MORE_NAME to binding.name2.text.toString(),
                DATA.SHOW_MORE_BOOLEAN to (DATA.EMPTY + true)
            )
        }
        binding.showMore3.setOnClickListener {
            context?.openActivity<ShowMoreActivity>(
                DATA.SHOW_MORE_TYPE to DATA.LOVES_COUNT,
                DATA.SHOW_MORE_NAME to binding.name3.text.toString(),
                DATA.SHOW_MORE_BOOLEAN to (DATA.EMPTY + true)
            )
        }
        binding.showMore4.setOnClickListener {
            context?.openActivity<ShowMoreActivity>(
                DATA.SHOW_MORE_TYPE to DATA.TIMESTAMP,
                DATA.SHOW_MORE_NAME to binding.name4.text.toString(),
                DATA.SHOW_MORE_BOOLEAN to (DATA.EMPTY + true)
            )
        }
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryHomeAdapter { category ->
            context?.openActivity<CategoryDetailsActivity>(
                DATA.CATEGORY_ID to category.id, DATA.CATEGORY_NAME to category.name
            )
        }
        binding.recyclerCategory.adapter = categoryAdapter

        editorsChoiceAdapter = MovieAdapter(false) { movie ->
            context?.openActivity<MovieDetailsActivity>(
                DATA.MOVIE_ID to movie.id, DATA.MOVIE_LINK to movie.movieLink
            )
        }
        binding.recyclerView.adapter = editorsChoiceAdapter

        mostViewedAdapter = MovieAdapter(false) { movie ->
            context?.openActivity<MovieDetailsActivity>(
                DATA.MOVIE_ID to movie.id, DATA.MOVIE_LINK to movie.movieLink
            )
        }
        binding.recyclerView2.adapter = mostViewedAdapter

        mostLovedAdapter = MovieAdapter(false) { movie ->
            context?.openActivity<MovieDetailsActivity>(
                DATA.MOVIE_ID to movie.id, DATA.MOVIE_LINK to movie.movieLink
            )
        }
        binding.recyclerView3.adapter = mostLovedAdapter

        newMoviesAdapter = MovieAdapter(false) { movie ->
            context?.openActivity<MovieDetailsActivity>(
                DATA.MOVIE_ID to movie.id, DATA.MOVIE_LINK to movie.movieLink
            )
        }
        binding.recyclerView4.adapter = newMoviesAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                Timber.d("Categories collected: %d", categories.size)
                categoryAdapter.submitList(categories)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sliderCount.collect { count ->
                Timber.d("Slider count collected: %d", count)
                // Firebase logic here is better, but this handles the count
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sliderImages.collect { images ->
                binding.imageSlider.setSliderAdapter(ImageSliderAdapter(images) { _ ->
                    // Handle click
                })
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editorsChoiceMovies.collect { movies ->
                Timber.d("Editors choice movies collected: %d", movies.size)
                updateMovieList(
                    movies, editorsChoiceAdapter, binding.bar, binding.recyclerView, binding.empty
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mostViewedMovies.collect { movies ->
                Timber.d("Most viewed movies collected: %d", movies.size)
                updateMovieList(
                    movies, mostViewedAdapter, binding.bar2, binding.recyclerView2, binding.empty2
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mostLovedMovies.collect { movies ->
                Timber.d("Most loved movies collected: %d", movies.size)
                updateMovieList(
                    movies, mostLovedAdapter, binding.bar3, binding.recyclerView3, binding.empty3
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newMovies.collect { movies ->
                Timber.d("New movies collected: %d", movies.size)
                updateMovieList(
                    movies, newMoviesAdapter, binding.bar4, binding.recyclerView4, binding.empty4
                )
            }
        }
    }

    private fun updateMovieList(
        movies: List<Movie>, adapter: MovieAdapter, bar: View, recyclerView: View, empty: View
    ) {
        adapter.submitList(movies)
        bar.visibility = View.GONE
        if (movies.isNotEmpty()) {
            recyclerView.visibility = View.VISIBLE
            empty.visibility = View.GONE
        } else {
            recyclerView.visibility = View.GONE
            empty.visibility = View.VISIBLE
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}