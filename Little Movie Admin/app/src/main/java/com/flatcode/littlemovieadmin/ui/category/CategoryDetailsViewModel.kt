package com.flatcode.littlemovieadmin.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.Movie
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
class CategoryDetailsViewModel @Inject constructor(
    private val movieRepo: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryDetailsUiState())
    val uiState: StateFlow<CategoryDetailsUiState> = _uiState.asStateFlow()

    fun init(categoryId: String, categoryName: String?) {
        _uiState.update { it.copy(categoryId = categoryId, categoryName = categoryName) }
        getData(_uiState.value.currentType)
    }

    fun getData(orderBy: String) {
        val categoryId = _uiState.value.categoryId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentType = orderBy) }
            try {
                val movies = movieRepo.getMovies(orderBy).filter { it.categoryId == categoryId }
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        movies = movies,
                        count = movies.size
                    ) 
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading category movies")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class CategoryDetailsUiState(
    val categoryId: String? = null,
    val categoryName: String? = null,
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val count: Int = 0,
    val currentType: String = DATA.TIMESTAMP
)
