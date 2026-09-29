package com.flatcode.littlemovie.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlemovie.model.EditorsChoice
import com.flatcode.littlemovie.model.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface EditorsChoiceDao {

    @Query("SELECT movies.* FROM movies INNER JOIN editors_choice ON movies.id = editors_choice.movieId ORDER BY editors_choice.timestamp DESC")
    fun getEditorsChoiceMovies(): Flow<List<Movie>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEditorsChoices(list: List<EditorsChoice>)

    @Query("DELETE FROM editors_choice")
    suspend fun deleteAllEditorsChoices()
}