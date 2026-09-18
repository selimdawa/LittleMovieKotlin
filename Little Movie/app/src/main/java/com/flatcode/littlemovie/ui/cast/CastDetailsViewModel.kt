package com.flatcode.littlemovie.ui.cast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.model.Movie
import com.flatcode.littlemovie.repository.MovieRepository
import com.flatcode.littlemovie.repository.UserRepository
import com.flatcode.littlemovie.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CastDetailsViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    val movies: StateFlow<List<Movie>> = _movies

    private val _isInterested = MutableStateFlow(false)
    val isInterested: StateFlow<Boolean> = _isInterested

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadMovies(castId: String, orderBy: String) {
        _isLoading.value = true
        viewModelScope.launch {
            movieRepository.getMoviesByCastId(castId, orderBy).collectLatest { list ->
                _movies.value = list
                _isLoading.value = false
                Timber.d("Movies for cast %s updated: %d", castId, list.size)
            }
        }
    }

    fun checkInterest(castId: String) {
        val userId = DATA.FirebaseUserUid ?: return
        viewModelScope.launch {
            userRepository.isInterested(userId, DATA.CAST, castId).collectLatest {
                _isInterested.value = it
            }
        }
    }

    fun toggleInterest(castId: String) {
        val userId = DATA.FirebaseUserUid ?: return
        viewModelScope.launch {
            userRepository.toggleInterest(userId, DATA.CAST, castId, !isInterested.value)
        }
    }
}
