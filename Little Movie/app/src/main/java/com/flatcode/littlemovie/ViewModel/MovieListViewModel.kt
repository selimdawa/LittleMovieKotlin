package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Model.Movie
import com.flatcode.littlemovie.Repository.MovieRepository
import com.flatcode.littlemovie.Unit.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _moviesCount = MutableStateFlow(0)
    val moviesCount: StateFlow<Int> = _moviesCount

    fun loadMovies(orderBy: String, reverse: Boolean = true) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getMovies(orderBy, reverse = reverse).collectLatest { list ->
                _movies.value = list
                _moviesCount.value = list.size
                _isLoading.value = false
                Timber.d("Movies updated: %d", list.size)
            }
        }
    }

    fun loadMoviesByCategory(categoryId: String, orderBy: String) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getMoviesByCategory(categoryId, orderBy).collectLatest { list ->
                _movies.value = list
                _moviesCount.value = list.size
                _isLoading.value = false
                Timber.d("Movies updated for category %s: %d", categoryId, list.size)
            }
        }
    }
}
