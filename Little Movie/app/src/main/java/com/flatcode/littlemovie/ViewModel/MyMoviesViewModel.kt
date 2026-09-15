package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Model.Movie
import com.flatcode.littlemovie.Repository.MovieRepository
import com.flatcode.littlemovie.Repository.UserRepository
import com.flatcode.littlemovie.Unit.DATA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class MyMoviesViewModel : ViewModel() {

    private val movieRepository = MovieRepository()
    private val userRepository = UserRepository()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadMovies(orderBy: String) {
        val userId = DATA.FirebaseUserUid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            userRepository.getInterestedCategories(userId).collectLatest { categoryIds ->
                if (categoryIds.isEmpty()) {
                    _movies.value = emptyList()
                    _isLoading.value = false
                    return@collectLatest
                }
                movieRepository.getMoviesByCategoryIds(categoryIds, orderBy).collectLatest { list ->
                    _movies.value = list
                    _isLoading.value = false
                    Timber.d("My Movies updated: %d", list.size)
                }
            }
        }
    }
}
