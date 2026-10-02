package com.flatcode.littlemovieadmin.repository

import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.utils.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val database: FirebaseDatabase,
) {
    private val moviesRef = database.getReference(DATA.MOVIES)

    fun getMovies(orderBy: String): Flow<List<Movie>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children.mapNotNull { it.getValue(Movie::class.java) }
                val sorted = when (orderBy) {
                    DATA.NAME -> list.sortedBy { it.name }
                    DATA.VIEWS_COUNT -> list.sortedByDescending { it.viewsCount }
                    DATA.LOVES_COUNT -> list.sortedByDescending { it.lovesCount }
                    else -> list.sortedByDescending { it.timestamp }
                }
                trySend(sorted)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting movies")
                close(error.toException())
            }
        }
        moviesRef.addValueEventListener(listener)
        awaitClose { moviesRef.removeEventListener(listener) }
    }

    fun getFavorites(uid: String, orderBy: String): Flow<List<Movie>> = callbackFlow {
        val favoritesRef = database.getReference(DATA.FAVORITES).child(uid)
        val listener = object : ValueEventListener {
            override fun onDataChange(favSnapshot: DataSnapshot) {
                val favIds = favSnapshot.children.mapNotNull { it.key }
                moviesRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(moviesSnapshot: DataSnapshot) {
                        val list = moviesSnapshot.children.mapNotNull { it.getValue(Movie::class.java) }
                            .filter { favIds.contains(it.id) }
                        val sorted = when (orderBy) {
                            DATA.NAME -> list.sortedBy { it.name }
                            DATA.VIEWS_COUNT -> list.sortedByDescending { it.viewsCount }
                            DATA.LOVES_COUNT -> list.sortedByDescending { it.lovesCount }
                            else -> list.sortedByDescending { it.timestamp }
                        }
                        trySend(sorted)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Timber.e(error.toException(), "Error getting favorite movies")
                        close(error.toException())
                    }
                })
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.toException(), "Error getting favorites list")
                close(error.toException())
            }
        }
        favoritesRef.addValueEventListener(listener)
        awaitClose { favoritesRef.removeEventListener(listener) }
    }

    suspend fun getMovie(movieId: String): Movie? {
        return try {
            moviesRef.child(movieId).get().await().getValue(Movie::class.java)
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getFavoriteMovieIds(uid: String): List<String> {
        return try {
            val snapshot = database.getReference(DATA.FAVORITES).child(uid).get().await()
            snapshot.children.mapNotNull { it.key }
        } catch (_: Exception) {
            emptyList()
        }
    }

    suspend fun addMovie(movie: Movie, movieId: String) {
        moviesRef.child(movieId).setValue(movie).await()
    }

    suspend fun updateMovie(movieId: String, updates: Map<String, Any?>) {
        moviesRef.child(movieId).updateChildren(updates).await()
    }
}
