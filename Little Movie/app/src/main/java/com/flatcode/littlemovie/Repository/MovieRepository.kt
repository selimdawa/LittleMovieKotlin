package com.flatcode.littlemovie.repository

import com.flatcode.littlemovie.db.FavoriteDao
import com.flatcode.littlemovie.db.InterestedDao
import com.flatcode.littlemovie.db.MovieDao
import com.flatcode.littlemovie.model.Cast
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.model.Comment
import com.flatcode.littlemovie.model.FavoriteEntity
import com.flatcode.littlemovie.model.InterestedEntity
import com.flatcode.littlemovie.model.Movie
import com.flatcode.littlemovie.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val movieDao: MovieDao,
    private val favoriteDao: FavoriteDao,
    private val interestedDao: InterestedDao
) {

    private val database = FirebaseDatabase.getInstance()
    private val moviesRef = database.getReference(DATA.MOVIES)
    private val castMovieRef = database.getReference(DATA.CAST_MOVIE)
    private val favoritesRef = database.getReference(DATA.FAVORITES)
    private val lovesRef = database.getReference(DATA.LOVES)

    fun getMovies(orderBy: String, limit: Int? = null, reverse: Boolean = true): Flow<List<Movie>> {
        syncMovies(orderBy, limit)
        val flow = when (orderBy) {
            DATA.EDITORS_CHOICE -> movieDao.getEditorsChoiceMovies()
            DATA.VIEWS_COUNT -> movieDao.getMostViewedMovies(limit ?: 100)
            DATA.LOVES_COUNT -> movieDao.getMostLovedMovies()
            else -> movieDao.getLatestMovies(limit ?: 100)
        }
        return if (reverse) flow else flow.map { it.reversed() }
    }

    fun getMoviesByCategory(categoryId: String, orderBy: String): Flow<List<Movie>> {
        syncMovies(orderBy, null)
        return movieDao.getMoviesByCategory(categoryId)
    }

    fun getMoviesByCastId(castId: String, orderBy: String): Flow<List<Movie>> {
        syncMovies(orderBy, null)
        Timber.d("Fetching movies for cast %s with orderBy %s", castId, orderBy)
        return movieDao.getAllMovies()
    }

    fun getFavoriteMovies(userId: String, orderBy: String): Flow<List<Movie>> {
        syncMovies(orderBy, null)
        syncFavorites(userId)
        return favoriteDao.getFavoriteMovies(userId)
    }

    @Suppress("unused")
    fun getFavoriteCount(userId: String): Flow<Int> {
        syncFavorites(userId)
        return favoriteDao.getFavoriteCount(userId)
    }

    fun getInterestedCategories(userId: String): Flow<List<Category>> {
        syncInterested(userId, DATA.CATEGORIES)
        return interestedDao.getInterestedCategories(userId, DATA.CATEGORIES)
    }

    @Suppress("unused")
    fun getInterestedCasts(userId: String): Flow<List<Cast>> {
        syncInterested(userId, DATA.CAST)
        return interestedDao.getInterestedCasts(userId, DATA.CAST)
    }

    @Suppress("unused")
    fun getInterestedCount(userId: String, databaseName: String): Flow<Int> {
        syncInterested(userId, databaseName)
        return interestedDao.getInterestedCount(userId, databaseName)
    }

    suspend fun toggleFavorite(movieId: String, userId: String, isFavorite: Boolean) {
        try {
            if (isFavorite) {
                favoritesRef.child(userId).child(movieId).setValue(true).await()
                favoriteDao.insertFavorite(FavoriteEntity(userId, movieId))
            } else {
                favoritesRef.child(userId).child(movieId).removeValue().await()
                favoriteDao.deleteFavorite(userId, movieId)
            }
        } catch (e: Exception) {
            if (isFavorite) {
                favoriteDao.insertFavorite(FavoriteEntity(userId, movieId))
            } else {
                favoriteDao.deleteFavorite(userId, movieId)
            }
            Timber.e(e, "Error toggling favorite")
        }
    }

    @Suppress("unused")
    suspend fun toggleInterested(userId: String, databaseName: String, itemId: String, isInterested: Boolean) {
        val ref = database.getReference(DATA.INTERESTED).child(userId).child(databaseName).child(itemId)
        if (isInterested) {
            ref.setValue(true)
            interestedDao.insertInterested(InterestedEntity(userId, databaseName, itemId))
        } else {
            ref.removeValue()
            interestedDao.deleteInterested(userId, databaseName, itemId)
        }
    }

    private fun syncMovies(orderBy: String, limit: Int?) {
        var query: Query = moviesRef.orderByChild(orderBy)
        limit?.let { query = query.limitToLast(it) }

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Movie>()
                for (data in snapshot.children) {
                    val item = data.getValue(Movie::class.java) ?: continue
                    list.add(item)
                }
                CoroutineScope(Dispatchers.IO).launch {
                    movieDao.insertMovies(list)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "syncMovies failed")
            }
        })
    }

    private fun syncFavorites(userId: String) {
        if (userId.isEmpty()) return
        favoritesRef.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val favList = snapshot.children.mapNotNull { it.key }
                    .map { FavoriteEntity(userId, it) }
                CoroutineScope(Dispatchers.IO).launch {
                    favoriteDao.deleteAllFavoritesForUser(userId)
                    favoriteDao.insertFavorites(favList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "syncFavorites failed")
            }
        })
    }

    private fun syncInterested(userId: String, databaseName: String) {
        if (userId.isEmpty()) return
        database.getReference(DATA.INTERESTED).child(userId).child(databaseName)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = snapshot.children.mapNotNull { it.key }
                        .map { InterestedEntity(userId, databaseName, it) }
                    CoroutineScope(Dispatchers.IO).launch {
                        interestedDao.deleteAllInterestedForUser(userId, databaseName)
                        interestedDao.insertInterestedList(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Timber.e(error.toException(), "syncInterested failed")
                }
            })
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
        val listener = favoritesRef.child(userId).child(movieId)
            .addValueEventListener(object : ValueEventListener {
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
        val listener = lovesRef.child(movieId).child(userId)
            .addValueEventListener(object : ValueEventListener {
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
            moviesRef.child(movieId).child(DATA.LOVES_COUNT).setValue(currentLoves + increment)
                .await()
        } catch (e: Exception) {
            Timber.e(e, "Error updating loves count")
        }
    }

    fun getMovieComments(movieId: String): Flow<List<Comment>> = callbackFlow {
        val listener = moviesRef.child(movieId).child(DATA.COMMENTS)
            .addValueEventListener(object : ValueEventListener {
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

    suspend fun deleteComment(movieId: String, commentId: String): Result<Unit> {
        return try {
            moviesRef.child(movieId).child(DATA.COMMENTS).child(commentId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getMovieCastIds(movieId: String): Flow<List<String>> = callbackFlow {
        val listener =
            castMovieRef.child(movieId).addValueEventListener(object : ValueEventListener {
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
}