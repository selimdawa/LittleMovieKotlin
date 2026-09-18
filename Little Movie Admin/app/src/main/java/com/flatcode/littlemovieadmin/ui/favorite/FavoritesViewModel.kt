package com.flatcode.littlemovieadmin.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.repository.AuthRepository
import com.flatcode.littlemovieadmin.repository.MovieRepository
import com.flatcode.littlemovieadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    fun getData(orderBy: String) {
        val uid = authRepo.getCurrentUserUid() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentType = orderBy) }
            try {
                val favoriteIds = movieRepo.getFavoriteMovieIds(uid)
                val movies = movieRepo.getMovies(orderBy).filter { favoriteIds.contains(it.id) }
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        movies = movies,
                        count = movies.size
                    ) 
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading favorites")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val count: Int = 0,
    val currentType: String = DATA.TIMESTAMP
)
