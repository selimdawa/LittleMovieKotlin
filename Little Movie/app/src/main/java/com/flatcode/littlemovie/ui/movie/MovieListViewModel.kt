package com.flatcode.littlemovie.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.model.Movie
import com.flatcode.littlemovie.repository.MovieRepository
import com.flatcode.littlemovie.utils.DATA
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
        if (_movies.value.isEmpty()) {
            _isLoading.value = true
        }
        viewModelScope.launch {
            repository.getMovies(orderBy, reverse = reverse).collectLatest { list ->
                val sorted = sortMovies(list, orderBy)
                _movies.value = sorted
                _moviesCount.value = sorted.size
                _isLoading.value = false
                Timber.d("Movies updated: %d", sorted.size)
            }
        }
    }

    fun loadMoviesByCategory(categoryId: String, orderBy: String) {
        if (_movies.value.isNotEmpty()) {
            val sorted = sortMovies(_movies.value, orderBy)
            _movies.value = sorted
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            repository.getMoviesByCategory(categoryId, orderBy).collectLatest { list ->
                val sorted = sortMovies(list, orderBy)
                _movies.value = sorted
                _moviesCount.value = sorted.size
                _isLoading.value = false
                Timber.d("Movies updated for category %s: %d", categoryId, sorted.size)
            }
        }
    }

    private fun sortMovies(list: List<Movie>, orderBy: String): List<Movie> {
        return when (orderBy) {
            DATA.VIEWS_COUNT -> list.sortedByDescending { it.viewsCount }
            DATA.LOVES_COUNT -> list.sortedByDescending { it.lovesCount }
            DATA.NAME -> list.sortedBy { it.name?.lowercase() ?: "" }
            else -> list.sortedByDescending { it.timestamp }
        }
    }
}
