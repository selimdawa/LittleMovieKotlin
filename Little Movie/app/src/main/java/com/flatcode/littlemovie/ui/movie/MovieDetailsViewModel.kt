package com.flatcode.littlemovie.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.model.Cast
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.model.Comment
import com.flatcode.littlemovie.model.Movie
import com.flatcode.littlemovie.model.User
import com.flatcode.littlemovie.repository.CastRepository
import com.flatcode.littlemovie.repository.CategoryRepository
import com.flatcode.littlemovie.repository.MovieRepository
import com.flatcode.littlemovie.repository.UserRepository
import com.flatcode.littlemovie.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailsViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val castRepository: CastRepository,
    private val userRepository: UserRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _movie = MutableStateFlow<Movie?>(null)
    val movie: StateFlow<Movie?> = _movie

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments

    private val _cast = MutableStateFlow<List<Cast>>(emptyList())
    val cast: StateFlow<List<Cast>> = _cast

    private val _publisher = MutableStateFlow<User?>(null)
    val publisher: StateFlow<User?> = _publisher

    private val _category = MutableStateFlow<Category?>(null)
    val category: StateFlow<Category?> = _category

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite: StateFlow<Boolean> = _isFavorite

    private val _isLoved = MutableStateFlow(false)
    val isLoved: StateFlow<Boolean> = _isLoved

    private val _lovesCount = MutableStateFlow(0L)
    val lovesCount: StateFlow<Long> = _lovesCount

    private val _addCommentStatus = MutableStateFlow<Result<Unit>?>(null)
    val addCommentStatus: StateFlow<Result<Unit>?> = _addCommentStatus

    fun loadDetails(movieId: String) {
        val userId = DATA.FirebaseUserUid ?: return
        
        viewModelScope.launch {
            movieRepository.getMovieById(movieId).collectLatest {
                _movie.value = it
                it?.publisher?.let { publisherId -> loadPublisher(publisherId) }
                it?.categoryId?.let { categoryId -> loadCategory(categoryId) }
            }
        }

        viewModelScope.launch {
            movieRepository.getMovieComments(movieId).collectLatest {
                _comments.value = it
            }
        }

        viewModelScope.launch {
            movieRepository.getMovieCastIds(movieId).collectLatest { castIds ->
                if (castIds.isNotEmpty()) {
                    castRepository.getCastByIds(castIds).collectLatest {
                        _cast.value = it
                    }
                }
            }
        }

        viewModelScope.launch {
            movieRepository.isFavorite(movieId, userId).collectLatest {
                _isFavorite.value = it
            }
        }

        viewModelScope.launch {
            movieRepository.isLoved(movieId, userId).collectLatest {
                _isLoved.value = it
            }
        }

        viewModelScope.launch {
            movieRepository.getLovesCount(movieId).collectLatest {
                _lovesCount.value = it
            }
        }
        
        viewModelScope.launch {
            movieRepository.incrementViewCount(movieId)
        }
    }

    private fun loadPublisher(publisherId: String) {
        viewModelScope.launch {
            userRepository.getUserInfo(publisherId).collectLatest {
                _publisher.value = it
            }
        }
    }

    private fun loadCategory(categoryId: String) {
        viewModelScope.launch {
            categoryRepository.getCategoryById(categoryId).collectLatest {
                _category.value = it
            }
        }
    }

    fun toggleFavorite(movieId: String) {
        val userId = DATA.FirebaseUserUid ?: return
        viewModelScope.launch {
            movieRepository.toggleFavorite(movieId, userId, !_isFavorite.value)
        }
    }

    fun toggleLove(movieId: String) {
        val userId = DATA.FirebaseUserUid ?: return
        viewModelScope.launch {
            movieRepository.toggleLove(movieId, userId, !_isLoved.value)
        }
    }

    fun addComment(movieId: String, commentText: String) {
        viewModelScope.launch {
            val result = movieRepository.addComment(movieId, commentText)
            _addCommentStatus.value = result
        }
    }

    fun resetAddCommentStatus() {
        _addCommentStatus.value = null
    }
}
