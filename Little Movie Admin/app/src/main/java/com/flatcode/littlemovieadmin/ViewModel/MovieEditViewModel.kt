package com.flatcode.littlemovieadmin.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.Model.Movie
import com.flatcode.littlemovieadmin.Repository.CastRepository
import com.flatcode.littlemovieadmin.Repository.CategoryRepository
import com.flatcode.littlemovieadmin.Repository.MovieRepository
import com.flatcode.littlemovieadmin.Unit.DATA
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MovieEditViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    private val categoryRepo: CategoryRepository,
    private val castRepo: CastRepository,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieEditUiState())
    val uiState: StateFlow<MovieEditUiState> = _uiState.asStateFlow()

    fun init(movieId: String, initialCategoryId: String?) {
        _uiState.update { it.copy(movieId = movieId, initialCategoryId = initialCategoryId) }
        loadCategories()
        loadMovieInfo(movieId)
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val list = categoryRepo.getCategories(DATA.TIMESTAMP).map { 
                    CategoryInfo(it.id ?: "", it.name ?: "") 
                }
                _uiState.update { it.copy(categories = list) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading categories")
            }
        }
    }

    private fun loadMovieInfo(movieId: String) {
        viewModelScope.launch {
            try {
                val movie = movieRepo.getMovie(movieId)
                movie?.let { m ->
                    _uiState.update { it.copy(movie = m, selectedCategoryId = m.categoryId) }
                    m.categoryId?.let { catId ->
                        val category = categoryRepo.getCategory(catId)
                        _uiState.update { it.copy(selectedCategoryName = category?.name) }
                    }
                    
                    val castIds = castRepo.getMovieCastIds(movieId)
                    _uiState.update { it.copy(castIds = castIds, originalCastIds = castIds.toList()) }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading movie info")
            }
        }
    }

    fun updateMovie(
        name: String, description: String, year: Int, categoryId: String,
        imageUri: Uri?, castIds: List<String?>, onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val movieId = _uiState.value.movieId ?: throw Exception("Invalid Movie ID")
                
                val imageUrl = if (imageUri != null) {
                    val path = "Images/Movie/$movieId"
                    val ref = storage.getReference(path)
                    ref.putFile(imageUri).await()
                    ref.downloadUrl.await().toString()
                } else null

                val updates = mutableMapOf<String, Any?>().apply {
                    put(DATA.NAME, name)
                    put(DATA.DESCRIPTION, description)
                    put(DATA.YEAR, year)
                    put(DATA.CAST_COUNT, castIds.size)
                    put(DATA.CATEGORY_ID, categoryId)
                    imageUrl?.let { put(DATA.IMAGE, it) }
                }

                movieRepo.updateMovie(movieId, updates)
                
                // Handle category count changes
                val initialId = _uiState.value.initialCategoryId
                if (categoryId != initialId) {
                    val newCat = categoryRepo.getCategory(categoryId)
                    newCat?.let { categoryRepo.updateCategory(categoryId, mapOf(DATA.MOVIES_COUNT to (it.moviesCount + 1))) }
                    
                    initialId?.let { oldId ->
                        val oldCat = categoryRepo.getCategory(oldId)
                        oldCat?.let { categoryRepo.updateCategory(oldId, mapOf(DATA.MOVIES_COUNT to (it.moviesCount - 1).coerceAtLeast(0))) }
                    }
                }

                // Sync Cast counts
                val oldCastIds = _uiState.value.originalCastIds
                oldCastIds.forEach { id ->
                    if (!castIds.contains(id)) {
                        val cast = castRepo.getCast(id)
                        cast?.let { castRepo.updateCast(id, mapOf(DATA.MOVIES_COUNT to (it.moviesCount - 1).coerceAtLeast(0))) }
                    }
                }
                castIds.forEach { id ->
                    if (id != null && !oldCastIds.contains(id)) {
                        val cast = castRepo.getCast(id)
                        cast?.let { castRepo.updateCast(id, mapOf(DATA.MOVIES_COUNT to (it.moviesCount + 1))) }
                    }
                }

                val castMovieUpdates = mutableMapOf<String, Any>()
                castIds.forEach { it?.let { id -> castMovieUpdates[id] = true } }
                castRepo.updateMovieCast(movieId, castMovieUpdates)

                _uiState.update { it.copy(isLoading = false) }
                onResult(true, "Movie updated...")
            } catch (e: Exception) {
                Timber.e(e, "Error updating movie")
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, e.message)
            }
        }
    }
    
    fun setCategoryId(id: String, name: String) {
        _uiState.update { it.copy(selectedCategoryId = id, selectedCategoryName = name) }
    }
}

data class MovieEditUiState(
    val movieId: String? = null,
    val initialCategoryId: String? = null,
    val movie: Movie? = null,
    val categories: List<CategoryInfo> = emptyList(),
    val selectedCategoryId: String? = null,
    val selectedCategoryName: String? = null,
    val castIds: List<String> = emptyList(),
    val originalCastIds: List<String> = emptyList(),
    val isLoading: Boolean = false
)
