package com.flatcode.littlemovieadmin.ui.movie

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.repository.AuthRepository
import com.flatcode.littlemovieadmin.repository.CastRepository
import com.flatcode.littlemovieadmin.repository.CategoryRepository
import com.flatcode.littlemovieadmin.repository.MovieRepository
import com.flatcode.littlemovieadmin.utils.CloudinaryHelper
import com.flatcode.littlemovieadmin.utils.DATA
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MovieAddViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    private val categoryRepo: CategoryRepository,
    private val castRepo: CastRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieAddUiState())
    val uiState: StateFlow<MovieAddUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                val list = categoryRepo.getCategories(DATA.TIMESTAMP).map {
                    CategoryInfo(it.id, it.name ?: "")
                }
                _uiState.update { it.copy(categories = list) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading categories")
            }
        }
    }

    fun uploadMovie(
        name: String,
        description: String,
        year: Int,
        categoryId: String,
        imageUri: Uri,
        videoUri: Uri,
        durations: String?,
        castIds: List<String?>,
        onProgress: (Int) -> Unit,
        onResult: (Boolean, String?) -> Unit
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // 1. Upload Video
                val videoUrl =
                    CloudinaryHelper.uploadFile(videoUri, isVideo = true, onProgress = onProgress)

                // 2. Upload Image
                val imageUrl = CloudinaryHelper.uploadFile(imageUri)

                val movieRef = FirebaseDatabase.getInstance().getReference(DATA.MOVIES)
                val id = movieRef.push().key ?: throw Exception("Database error")

                // 3. Save Info
                val movie = Movie().apply {
                    this.id = id
                    this.publisher = authRepo.getCurrentUserUid()
                    this.timestamp = System.currentTimeMillis()
                    this.name = name
                    this.description = description
                    this.categoryId = categoryId
                    this.duration = durations
                    this.year = year
                    this.movieLink = videoUrl
                    this.image = imageUrl
                    this.editorsChoice = 0
                    this.lovesCount = 0
                    this.viewsCount = 0
                    this.castCount = castIds.size
                }

                movieRepo.addMovie(movie, id)

                // 4. Update Cast relationships
                val castUpdates = mutableMapOf<String, Any>()
                castIds.forEach { castId ->
                    castId?.let {
                        castUpdates[it] = true
                        // Update cast movie count (could be moved to a repository method that handles batch updates)
                        val cast = castRepo.getCast(it)
                        cast?.let { c ->
                            castRepo.updateCast(it, mapOf(DATA.MOVIES_COUNT to (c.moviesCount + 1)))
                        }
                    }
                }
                castRepo.updateMovieCast(id, castUpdates)

                // 5. Update Category movie count
                val category = categoryRepo.getCategory(categoryId)
                category?.let { cat ->
                    categoryRepo.updateCategory(
                        categoryId, mapOf(DATA.MOVIES_COUNT to (cat.moviesCount + 1))
                    )
                }

                _uiState.update { it.copy(isLoading = false) }
                onResult(true, "Successfully uploaded...")
            } catch (e: Exception) {
                Timber.e(e, "Error uploading movie")
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, e.message)
            }
        }
    }
}

data class MovieAddUiState(
    val isLoading: Boolean = false, val categories: List<CategoryInfo> = emptyList()
)

data class CategoryInfo(val id: String, val name: String)
