package com.flatcode.littlemovie.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlemovie.model.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY timestamp DESC")
    fun getAllMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE editorsChoice BETWEEN 1 AND 5 ORDER BY editorsChoice ASC")
    fun getEditorsChoiceMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies ORDER BY viewsCount DESC LIMIT :limit")
    fun getMostViewedMovies(limit: Int): Flow<List<Movie>>

    @Query("SELECT * FROM movies ORDER BY viewsCount DESC")
    fun getMostViewedMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies ORDER BY lovesCount DESC")
    fun getMostLovedMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM movies ORDER BY timestamp DESC LIMIT :limit")
    fun getLatestMovies(limit: Int): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE categoryId = :categoryId ORDER BY timestamp DESC")
    fun getMoviesByCategory(categoryId: String): Flow<List<Movie>>

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieById(id: String): Flow<Movie?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Delete
    suspend fun deleteMovie(movie: Movie)

    @Query("DELETE FROM movies")
    suspend fun deleteAllMovies()
}