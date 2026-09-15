package com.flatcode.littlemovieadmin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.Model.Movie
import com.flatcode.littlemovieadmin.Repository.MovieRepository
import com.flatcode.littlemovieadmin.Unit.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val repository: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MoviesUiState())
    val uiState: StateFlow<MoviesUiState> = _uiState.asStateFlow()

    fun getData(orderBy: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentType = orderBy) }
            try {
                val movies = repository.getMovies(orderBy)
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        movies = movies,
                        count = movies.size
                    ) 
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading movies")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class MoviesUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val count: Int = 0,
    val currentType: String = DATA.TIMESTAMP
)
