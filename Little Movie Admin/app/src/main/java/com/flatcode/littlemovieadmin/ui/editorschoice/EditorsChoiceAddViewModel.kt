package com.flatcode.littlemovieadmin.ui.editorschoice

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
class EditorsChoiceAddViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorsChoiceAddUiState())
    val uiState: StateFlow<EditorsChoiceAddUiState> = _uiState.asStateFlow()

    fun init(editorsChoiceId: String?, oldId: String?) {
        _uiState.update { it.copy(editorsChoiceId = editorsChoiceId, oldId = oldId) }
        getData(_uiState.value.currentType)
    }

    fun getData(orderBy: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentType = orderBy) }
            try {
                val movies = movieRepo.getMovies(orderBy).filter { it.editorsChoice == 0 }
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        movies = movies,
                        count = movies.size
                    ) 
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading movies for editors choice")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun getFavorites(orderBy: String) {
        val uid = authRepo.getCurrentUserUid() ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentType = orderBy) }
            try {
                val favoriteIds = movieRepo.getFavoriteMovieIds(uid)
                val movies = movieRepo.getMovies(orderBy).filter { 
                    favoriteIds.contains(it.id) && it.editorsChoice == 0 
                }
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        movies = movies,
                        count = movies.size
                    ) 
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading favorites for editors choice")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class EditorsChoiceAddUiState(
    val editorsChoiceId: String? = null,
    val oldId: String? = null,
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val count: Int = 0,
    val currentType: String = DATA.TIMESTAMP
)
