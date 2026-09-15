package com.flatcode.littlemovie.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.Adapter.CategoryHomeAdapter
import com.flatcode.littlemovie.Adapter.ImageSliderAdapter
import com.flatcode.littlemovie.Adapter.MovieAdapter
import com.flatcode.littlemovie.Model.Category
import com.flatcode.littlemovie.Model.Movie
import com.flatcode.littlemovie.Unit.CLASS
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Unit.VOID
import com.flatcode.littlemovie.ViewModel.HomeViewModel
import com.flatcode.littlemovie.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var binding: FragmentHomeBinding? = null
    private val viewModel: HomeViewModel by viewModels()

    private val editorsChoiceList = ArrayList<Movie?>()
    private val mostViewedList = ArrayList<Movie?>()
    private val mostLovedList = ArrayList<Movie?>()
    private val newMoviesList = ArrayList<Movie?>()
    private val categoryList = ArrayList<Category?>()

    private lateinit var categoryAdapter: CategoryHomeAdapter
    private lateinit var editorsChoiceAdapter: MovieAdapter
    private lateinit var mostViewedAdapter: MovieAdapter
    private lateinit var mostLovedAdapter: MovieAdapter
    private lateinit var newMoviesAdapter: MovieAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupClickListeners()
        setupAdapters()
        observeViewModel()

        return binding!!.root
    }

    private fun setupClickListeners() {
        binding!!.showMore.setOnClickListener {
            VOID.IntentExtra3(
                context, CLASS.SHOW_MORE, DATA.SHOW_MORE_TYPE,
                DATA.EDITORS_CHOICE, DATA.SHOW_MORE_NAME, binding!!.name.text.toString(),
                DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + false
            )
        }
        binding!!.showMore2.setOnClickListener {
            VOID.IntentExtra3(
                context, CLASS.SHOW_MORE, DATA.SHOW_MORE_TYPE,
                DATA.VIEWS_COUNT, DATA.SHOW_MORE_NAME, binding!!.mostViews.text.toString(),
                DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + true
            )
        }
        binding!!.showMore3.setOnClickListener {
            VOID.IntentExtra3(
                context, CLASS.SHOW_MORE, DATA.SHOW_MORE_TYPE,
                DATA.LOVES_COUNT, DATA.SHOW_MORE_NAME, binding!!.name3.text.toString(),
                DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + true
            )
        }
        binding!!.showMore4.setOnClickListener {
            VOID.IntentExtra3(
                context, CLASS.SHOW_MORE, DATA.SHOW_MORE_TYPE,
                DATA.TIMESTAMP, DATA.SHOW_MORE_NAME, binding!!.name4.text.toString(),
                DATA.SHOW_MORE_BOOLEAN, DATA.EMPTY + true
            )
        }
    }

    private fun setupAdapters() {
        categoryAdapter = CategoryHomeAdapter(context, categoryList)
        binding!!.recyclerCategory.adapter = categoryAdapter

        editorsChoiceAdapter = MovieAdapter(context, editorsChoiceList, false)
        binding!!.recyclerView.adapter = editorsChoiceAdapter

        mostViewedAdapter = MovieAdapter(context, mostViewedList, false)
        binding!!.recyclerView2.adapter = mostViewedAdapter

        mostLovedAdapter = MovieAdapter(context, mostLovedList, false)
        binding!!.recyclerView3.adapter = mostLovedAdapter

        newMoviesAdapter = MovieAdapter(context, newMoviesList, false)
        binding!!.recyclerView4.adapter = newMoviesAdapter
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.categories.collect { categories ->
                Timber.d("Categories collected: %d", categories.size)
                categoryList.clear()
                categoryList.addAll(categories)
                categoryAdapter.notifyDataSetChanged()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.sliderCount.collect { count ->
                Timber.d("Slider count collected: %d", count)
                binding!!.imageSlider.sliderAdapter = ImageSliderAdapter(context, count)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.editorsChoiceMovies.collect { movies ->
                Timber.d("Editors choice movies collected: %d", movies.size)
                updateMovieList(
                    movies, editorsChoiceList, editorsChoiceAdapter,
                    binding!!.bar, binding!!.recyclerView, binding!!.empty
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mostViewedMovies.collect { movies ->
                Timber.d("Most viewed movies collected: %d", movies.size)
                updateMovieList(
                    movies, mostViewedList, mostViewedAdapter,
                    binding!!.bar2, binding!!.recyclerView2, binding!!.empty2
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.mostLovedMovies.collect { movies ->
                Timber.d("Most loved movies collected: %d", movies.size)
                updateMovieList(
                    movies, mostLovedList, mostLovedAdapter,
                    binding!!.bar3, binding!!.recyclerView3, binding!!.empty3
                )
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.newMovies.collect { movies ->
                Timber.d("New movies collected: %d", movies.size)
                updateMovieList(
                    movies, newMoviesList, newMoviesAdapter,
                    binding!!.bar4, binding!!.recyclerView4, binding!!.empty4
                )
            }
        }
    }

    private fun updateMovieList(
        movies: List<Movie>, list: ArrayList<Movie?>, adapter: MovieAdapter,
        bar: View, recyclerView: View, empty: View
    ) {
        list.clear()
        list.addAll(movies)
        adapter.notifyDataSetChanged()
        bar.visibility = View.GONE
        if (list.isNotEmpty()) {
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
}