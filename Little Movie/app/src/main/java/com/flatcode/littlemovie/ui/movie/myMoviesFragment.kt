package com.flatcode.littlemovie.ui.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.flatcode.littlemovie.model.Movie
import com.flatcode.littlemovie.utils.DATA
import com.flatcode.littlemovie.databinding.FragmentMyMoviesBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class myMoviesFragment : Fragment() {

    private var _binding: FragmentMyMoviesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyMoviesViewModel by viewModels()
    private lateinit var adapter: MovieAdapter
    private var type: String = DATA.TIMESTAMP

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyMoviesBinding.inflate(inflater, container, false)

        setupAdapter()
        setupClickListeners()
        observeViewModel()

        return binding.root
    }

    private fun setupAdapter() {
        adapter = MovieAdapter(context, true)
        binding.recyclerView.adapter = adapter
    }

    private fun setupClickListeners() {
        binding.switchBar.all.setOnClickListener {
            type = DATA.TIMESTAMP
            viewModel.loadMovies(type)
        }
        binding.switchBar.mostViews.setOnClickListener {
            type = DATA.VIEWS_COUNT
            viewModel.loadMovies(type)
        }
        binding.switchBar.mostLoves.setOnClickListener {
            type = DATA.LOVES_COUNT
            viewModel.loadMovies(type)
        }
        binding.switchBar.name.setOnClickListener {
            type = DATA.NAME
            viewModel.loadMovies(type)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.movies.collect { movies ->
                adapter.submitList(movies)
                
                binding.progress.visibility = View.GONE
                if (movies.isNotEmpty()) {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.emptyText.visibility = View.GONE
                } else {
                    binding.recyclerView.visibility = View.GONE
                    binding.emptyText.visibility = View.VISIBLE
                }
            }
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                binding.progress.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.loadMovies(type)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
