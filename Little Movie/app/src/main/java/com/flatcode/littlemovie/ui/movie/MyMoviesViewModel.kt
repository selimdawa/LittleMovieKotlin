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
class MyMoviesViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadMovies(orderBy: String) {
        val userId = DATA.FirebaseUserUid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            movieRepository.getInterestedCategories(userId).collectLatest { categories ->
                val categoryIds = categories.map { it.id }
                movieRepository.getMovies(orderBy).collectLatest { allMovies ->
                    val filtered = if (categoryIds.isNotEmpty()) {
                        allMovies.filter { it.categoryId in categoryIds }
                    } else {
                        allMovies
                    }
                    _movies.value = filtered
                    _isLoading.value = false
                    Timber.d("My Movies updated: %d", filtered.size)
                }
            }
        }
    }
}