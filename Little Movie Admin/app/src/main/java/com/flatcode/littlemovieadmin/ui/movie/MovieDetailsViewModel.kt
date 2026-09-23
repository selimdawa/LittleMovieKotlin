package com.flatcode.littlemovieadmin.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.Cast
import com.flatcode.littlemovieadmin.model.Comment
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.model.User
import com.flatcode.littlemovieadmin.repository.CastRepository
import com.flatcode.littlemovieadmin.repository.MovieRepository
import com.flatcode.littlemovieadmin.repository.UserRepository
import com.flatcode.littlemovieadmin.utils.DATA
import com.google.firebase.database.FirebaseDatabase
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
class MovieDetailsViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    private val userRepo: UserRepository,
    private val castRepo: CastRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailsUiState())
    val uiState: StateFlow<MovieDetailsUiState> = _uiState.asStateFlow()

    fun setMovieId(movieId: String) {
        _uiState.update { it.copy(movieId = movieId) }
        loadData()
    }

    fun loadData() {
        val movieId = _uiState.value.movieId ?: return
        viewModelScope.launch {
            try {
                val movie = movieRepo.getMovie(movieId)
                movie?.let { m ->
                    _uiState.update { it.copy(movie = m) }
                    m.publisher?.let { pubId ->
                        val publisher = userRepo.getUserInfo(pubId)
                        _uiState.update { it.copy(publisher = publisher) }
                    }
                }

                loadComments(movieId)

                val castIds = castRepo.getMovieCastIds(movieId)
                val allCast = castRepo.getCastList(DATA.TIMESTAMP)
                val filteredCast = allCast.filter { castIds.contains(it.id) }
                _uiState.update { it.copy(castList = filteredCast) }

            } catch (e: Exception) {
                Timber.e(e, "Error loading movie details")
            }
        }
    }

    private suspend fun loadComments(movieId: String) {
        val snapshot = FirebaseDatabase.getInstance().getReference(DATA.MOVIES).child(movieId)
            .child(DATA.COMMENTS).get().await()
        val list = snapshot.children.mapNotNull { it.getValue(Comment::class.java) }
        _uiState.update { it.copy(comments = list) }
    }

    fun addComment(commentText: String, onResult: (Boolean, String?) -> Unit) {
        val movieId = _uiState.value.movieId ?: return
        viewModelScope.launch {
            try {
                val ref = FirebaseDatabase.getInstance().getReference(DATA.MOVIES).child(movieId)
                    .child(DATA.COMMENTS)
                val id = ref.push().key ?: return@launch

                val hashMap = HashMap<String, Any?>().apply {
                    put(DATA.ID, id)
                    put(DATA.MOVIE_ID, movieId)
                    put(DATA.TIMESTAMP, System.currentTimeMillis())
                    put(DATA.COMMENT, commentText)
                    put(DATA.PUBLISHER, DATA.FirebaseUserUid)
                }

                ref.child(id).setValue(hashMap).await()
                loadComments(movieId)
                onResult(true, "Comment Added...")
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }
}

data class MovieDetailsUiState(
    val movieId: String? = null,
    val movie: Movie? = null,
    val publisher: User? = null,
    val comments: List<Comment> = emptyList(),
    val castList: List<Cast> = emptyList()
)
