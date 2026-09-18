package com.flatcode.littlemovieadmin.repository

import com.flatcode.littlemovieadmin.db.MovieDao
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.utils.DATA
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
    private val database: FirebaseDatabase,
    private val movieDao: MovieDao
) {
    private val moviesRef = database.getReference(DATA.MOVIES)

    val allMovies: Flow<List<Movie>> = movieDao.getAllMovies()

    suspend fun getMovies(orderBy: String): List<Movie> {
        return try {
            val snapshot = moviesRef.orderByChild(orderBy).get().await()
            val movies = snapshot.children.mapNotNull { it.getValue(Movie::class.java) }.reversed()
            movieDao.insertMovies(movies)
            movies
        } catch (e: Exception) {
            // In case of error (e.g. offline), return from Room if needed, 
            // though getMovies usually expects a fresh list.
            emptyList()
        }
    }

    suspend fun getMovie(movieId: String): Movie? {
        return try {
            val movie = moviesRef.child(movieId).get().await().getValue(Movie::class.java)
            movie?.let { movieDao.insertMovie(it) }
            movie
        } catch (e: Exception) {
            movieDao.getMovieById(movieId)
        }
    }

    suspend fun getFavoriteMovieIds(uid: String): List<String> {
        val snapshot = database.getReference(DATA.FAVORITES).child(uid).get().await()
        return snapshot.children.mapNotNull { it.key }
    }

    suspend fun addMovie(movie: Movie, movieId: String) {
        moviesRef.child(movieId).setValue(movie).await()
        movieDao.insertMovie(movie)
    }

    suspend fun updateMovie(movieId: String, updates: Map<String, Any?>) {
        moviesRef.child(movieId).updateChildren(updates).await()
        // Sync local
        val updatedMovie = getMovie(movieId)
        updatedMovie?.let { movieDao.insertMovie(it) }
    }

    suspend fun removeMovie(movieId: String) {
        moviesRef.child(movieId).removeValue().await()
        val movie = movieDao.getMovieById(movieId)
        movie?.let { movieDao.deleteMovie(it) }
    }

    suspend fun incrementCount(movieId: String, field: String) {
        moviesRef.child(movieId).child(field).setValue(ServerValue.increment(1)).await()
        val movie = getMovie(movieId)
        movie?.let { movieDao.insertMovie(it) }
    }

    suspend fun decrementCount(movieId: String, field: String) {
        moviesRef.child(movieId).child(field).setValue(ServerValue.increment(-1)).await()
        val movie = getMovie(movieId)
        movie?.let { movieDao.insertMovie(it) }
    }
}
