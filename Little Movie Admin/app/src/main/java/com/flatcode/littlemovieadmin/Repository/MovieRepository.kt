package com.flatcode.littlemovieadmin.Repository

import com.flatcode.littlemovieadmin.Model.Movie
import com.flatcode.littlemovieadmin.Unit.DATA
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val database: FirebaseDatabase
) {
    private val moviesRef = database.getReference(DATA.MOVIES)

    suspend fun getMovies(orderBy: String): List<Movie> {
        val snapshot = moviesRef.orderByChild(orderBy).get().await()
        return snapshot.children.mapNotNull { it.getValue(Movie::class.java) }.reversed()
    }

    suspend fun getMovie(movieId: String): Movie? =
        moviesRef.child(movieId).get().await().getValue(Movie::class.java)

    suspend fun getFavoriteMovieIds(uid: String): List<String> {
        val snapshot = database.getReference(DATA.FAVORITES).child(uid).get().await()
        return snapshot.children.mapNotNull { it.key }
    }

    suspend fun addMovie(movie: Movie, movieId: String) =
        moviesRef.child(movieId).setValue(movie).await()

    suspend fun updateMovie(movieId: String, updates: Map<String, Any?>) =
        moviesRef.child(movieId).updateChildren(updates).await()

    suspend fun removeMovie(movieId: String) =
        moviesRef.child(movieId).removeValue().await()

    suspend fun incrementCount(movieId: String, field: String) =
        moviesRef.child(movieId).child(field).setValue(ServerValue.increment(1)).await()

    suspend fun decrementCount(movieId: String, field: String) =
        moviesRef.child(movieId).child(field).setValue(ServerValue.increment(-1)).await()
}
