package com.flatcode.littlemovie.repository

import com.flatcode.littlemovie.db.MovieDao
import com.flatcode.littlemovie.model.Comment
import com.flatcode.littlemovie.model.Movie
import com.flatcode.littlemovie.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val movieDao: MovieDao
) {

    private val database = FirebaseDatabase.getInstance()
    private val moviesRef = database.getReference(DATA.MOVIES)
    private val castMovieRef = database.getReference(DATA.CAST_MOVIE)
    private val favoritesRef = database.getReference(DATA.FAVORITES)
    private val lovesRef = database.getReference(DATA.LOVES)

    fun getMovies(orderBy: String, limit: Int? = null, reverse: Boolean = true): Flow<List<Movie>> = callbackFlow {
        Timber.d("Fetching movies ordered by %s", orderBy)
        var query: Query = moviesRef.orderByChild(orderBy)
        
        limit?.let { query = query.limitToLast(it) }

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Movie>()
                for (data in snapshot.children) {
                    val item = data.getValue(Movie::class.java)
                    item?.let {
                        if (orderBy == DATA.EDITORS_CHOICE) {
                            if (it.editorsChoice > 0) {
                                list.add(it)
                            }
                        } else {
                            list.add(it)
                        }
                    }
                }
                if (reverse) {
                    list.reverse()
                }
                // Update Local Room Database
                launch {
                    movieDao.insertMovies(list)
                }
                trySend(list)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e("Error fetching movies: %s", error.message)
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    fun getMoviesByCategory(categoryId: String, orderBy: String): Flow<List<Movie>> = callbackFlow {
        val query = moviesRef.orderByChild(orderBy)
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Movie>()
                for (data in snapshot.children) {
                    val movie = data.getValue(Movie::class.java)
                    movie?.let {
                        if (it.categoryId == categoryId) {
                            list.add(it)
                        }
                    }
                }
                list.reverse()
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    fun getMoviesByCategoryIds(categoryIds: List<String>, orderBy: String): Flow<List<Movie>> = callbackFlow {
        val query = moviesRef.orderByChild(orderBy)
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Movie>()
                for (data in snapshot.children) {
                    val movie = data.getValue(Movie::class.java)
                    movie?.let {
                        if (categoryIds.contains(it.categoryId)) {
                            list.add(it)
                        }
                    }
                }
                list.reverse()
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { query.removeEventListener(listener) }
    }

    fun getMoviesByCastId(castId: String, orderBy: String): Flow<List<Movie>> = callbackFlow {
        val castMovieListener = castMovieRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val movieIds = mutableListOf<String>()
                for (movieSnapshot in snapshot.children) {
                    if (movieSnapshot.hasChild(castId)) {
                        movieSnapshot.key?.let { movieIds.add(it) }
                    }
                }
                
                moviesRef.orderByChild(orderBy).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(movieSnapshot: DataSnapshot) {
                        val list = mutableListOf<Movie>()
                        for (data in movieSnapshot.children) {
                            val movie = data.getValue(Movie::class.java)
                            if (movie != null && movieIds.contains(movie.id)) {
                                list.add(movie)
                            }
                        }
                        list.reverse()
                        trySend(list)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        close(error.toException())
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { castMovieRef.removeEventListener(castMovieListener) }
    }

    fun getFavoriteMovies(userId: String, orderBy: String): Flow<List<Movie>> = callbackFlow {
        val favListener = favoritesRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val movieIds = mutableListOf<String>()
                for (data in snapshot.children) {
                    data.key?.let { movieIds.add(it) }
                }
                
                moviesRef.orderByChild(orderBy).addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(movieSnapshot: DataSnapshot) {
                        val list = mutableListOf<Movie>()
                        for (data in movieSnapshot.children) {
                            val movie = data.getValue(Movie::class.java)
                            if (movie != null && movieIds.contains(movie.id)) {
                                list.add(movie)
                            }
                        }
                        list.reverse()
                        trySend(list)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        close(error.toException())
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { favoritesRef.child(userId).removeEventListener(favListener) }
    }

    fun getMovieById(movieId: String): Flow<Movie?> = callbackFlow {
        val listener = moviesRef.child(movieId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Movie::class.java))
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { moviesRef.child(movieId).removeEventListener(listener) }
    }

    fun isFavorite(movieId: String, userId: String): Flow<Boolean> = callbackFlow {
        val listener = favoritesRef.child(userId).child(movieId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.exists())
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { favoritesRef.child(userId).child(movieId).removeEventListener(listener) }
    }

    fun isLoved(movieId: String, userId: String): Flow<Boolean> = callbackFlow {
        val listener = lovesRef.child(movieId).child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.exists())
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { lovesRef.child(movieId).child(userId).removeEventListener(listener) }
    }

    fun getLovesCount(movieId: String): Flow<Long> = callbackFlow {
        val listener = lovesRef.child(movieId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { lovesRef.child(movieId).removeEventListener(listener) }
    }

    suspend fun incrementViewCount(movieId: String) {
        try {
            val snapshot = moviesRef.child(movieId).child(DATA.VIEWS_COUNT).get().await()
            val currentViews = snapshot.getValue(Long::class.java) ?: 0L
            moviesRef.child(movieId).child(DATA.VIEWS_COUNT).setValue(currentViews + 1).await()
        } catch (e: Exception) {
            Timber.e(e, "Error incrementing view count")
        }
    }

    suspend fun toggleFavorite(movieId: String, userId: String, isFavorite: Boolean) {
        try {
            if (isFavorite) {
                favoritesRef.child(userId).child(movieId).setValue(true).await()
            } else {
                favoritesRef.child(userId).child(movieId).removeValue().await()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error toggling favorite")
        }
    }

    suspend fun toggleLove(movieId: String, userId: String, isLoved: Boolean) {
        try {
            if (isLoved) {
                lovesRef.child(movieId).child(userId).setValue(true).await()
                updateLovesCount(movieId, 1)
            } else {
                lovesRef.child(movieId).child(userId).removeValue().await()
                updateLovesCount(movieId, -1)
            }
        } catch (e: Exception) {
            Timber.e(e, "Error toggling love")
        }
    }

    private suspend fun updateLovesCount(movieId: String, increment: Long) {
        try {
            val snapshot = moviesRef.child(movieId).child(DATA.LOVES_COUNT).get().await()
            val currentLoves = snapshot.getValue(Long::class.java) ?: 0L
            moviesRef.child(movieId).child(DATA.LOVES_COUNT).setValue(currentLoves + increment).await()
        } catch (e: Exception) {
            Timber.e(e, "Error updating loves count")
        }
    }

    fun getMovieComments(movieId: String): Flow<List<Comment>> = callbackFlow {
        val listener = moviesRef.child(movieId).child(DATA.COMMENTS).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Comment>()
                for (data in snapshot.children) {
                    data.getValue(Comment::class.java)?.let { list.add(it) }
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { moviesRef.child(movieId).child(DATA.COMMENTS).removeEventListener(listener) }
    }

    suspend fun addComment(movieId: String, commentText: String): Result<Unit> {
        return try {
            val id = moviesRef.push().key ?: throw Exception("Failed to get push key")
            val hashMap = HashMap<String, Any?>()
            hashMap[DATA.ID] = id
            hashMap[DATA.MOVIE_ID] = movieId
            hashMap[DATA.TIMESTAMP] = System.currentTimeMillis()
            hashMap[DATA.COMMENT] = commentText
            hashMap[DATA.PUBLISHER] = DATA.FirebaseUserUid
            moviesRef.child(movieId).child(DATA.COMMENTS).child(id).setValue(hashMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getMovieCastIds(movieId: String): Flow<List<String>> = callbackFlow {
        val listener = castMovieRef.child(movieId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<String>()
                for (data in snapshot.children) {
                    data.key?.let { list.add(it) }
                }
                trySend(list)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { castMovieRef.child(movieId).removeEventListener(listener) }
    }

    fun getSliderCount(): Flow<Int> = callbackFlow {
        val ref = database.getReference(DATA.SLIDER_SHOW)
        val listener = ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.childrenCount.toInt())
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })
        awaitClose { ref.removeEventListener(listener) }
    }
}
