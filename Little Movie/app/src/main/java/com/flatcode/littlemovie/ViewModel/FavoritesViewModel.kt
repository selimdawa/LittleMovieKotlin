package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Model.Movie
import com.flatcode.littlemovie.Repository.MovieRepository
import com.flatcode.littlemovie.Unit.DATA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class FavoritesViewModel : ViewModel() {

    private val repository = MovieRepository()

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadMovies(orderBy: String) {
        val userId = DATA.FirebaseUserUid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            repository.getFavoriteMovies(userId, orderBy).collectLatest { list ->
                _movies.value = list
                _isLoading.value = false
                Timber.d("Favorite movies updated: %d", list.size)
            }
        }
    }
}
