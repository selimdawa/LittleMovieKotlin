package com.flatcode.littlemovieadmin.ui.cast

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.repository.CastRepository
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
class CastDetailsViewModel @Inject constructor(
    private val castRepo: CastRepository,
    private val movieRepo: MovieRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CastDetailsUiState())
    val uiState: StateFlow<CastDetailsUiState> = _uiState.asStateFlow()

    fun init(castId: String, castName: String?, castImage: String?, castAbout: String?) {
        _uiState.update { 
            it.copy(
                castId = castId, 
                castName = castName, 
                castImage = castImage, 
                castAbout = castAbout
            ) 
        }
        getData(_uiState.value.currentType)
    }

    fun getData(orderBy: String) {
        val castId = _uiState.value.castId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentType = orderBy) }
            try {
                // To find movies for a cast, we can either:
                // 1. Scan all CAST_MOVIE entries (inefficient but works with existing schema)
                // 2. Have a list of movie IDs in the CAST object (missing in schema)
                // Existing logic:
                // for (snapshot in dataSnapshot.children) { // snapshot is movieId
                //    if (snapshot.hasChild(castId)) { ... }
                // }
                
                // Let's implement this in CastRepository if possible or do it here.
                val movieIds = castRepo.getMovieCastIds(castId) // Wait, this name is confusing in repo.
                // In CastRepository: getMovieCastIds(movieId) gets cast IDs.
                // We need something to get movie IDs for a cast ID.
                
                // Let's use the scan logic but cleaner.
                val movies = movieRepo.getMovies(orderBy).filter { movie ->
                    val castIds = castRepo.getMovieCastIds(movie.id ?: "")
                    castIds.contains(castId)
                }
                
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        movies = movies,
                        count = movies.size
                    ) 
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading cast details")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class CastDetailsUiState(
    val castId: String? = null,
    val castName: String? = null,
    val castImage: String? = null,
    val castAbout: String? = null,
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val count: Int = 0,
    val currentType: String = DATA.TIMESTAMP
)
