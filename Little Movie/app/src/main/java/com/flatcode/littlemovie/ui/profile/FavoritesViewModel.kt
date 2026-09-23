package com.flatcode.littlemovie.ui.profile

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
class FavoritesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

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