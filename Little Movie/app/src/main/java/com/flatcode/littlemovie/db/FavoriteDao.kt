package com.flatcode.littlemovie.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlemovie.model.FavoriteEntity
import com.flatcode.littlemovie.model.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("SELECT movies.* FROM movies INNER JOIN favorites ON movies.id = favorites.movieId WHERE favorites.userId = :userId ORDER BY movies.timestamp DESC")
    fun getFavoriteMovies(userId: String): Flow<List<Movie>>

    @Query("SELECT COUNT(movies.id) FROM movies INNER JOIN favorites ON movies.id = favorites.movieId WHERE favorites.userId = :userId")
    fun getFavoriteCount(userId: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorites(favorites: List<FavoriteEntity>)

    @Query("DELETE FROM favorites WHERE userId = :userId AND movieId = :movieId")
    suspend fun deleteFavorite(userId: String, movieId: String)

    @Query("DELETE FROM favorites WHERE userId = :userId")
    suspend fun deleteAllFavoritesForUser(userId: String)
}
