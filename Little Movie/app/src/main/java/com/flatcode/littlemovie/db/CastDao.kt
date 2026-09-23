package com.flatcode.littlemovie.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlemovie.model.Cast
import kotlinx.coroutines.flow.Flow

@Dao
interface CastDao {
    @Query("SELECT * FROM casts ORDER BY name ASC")
    fun getAllCasts(): Flow<List<Cast>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCasts(casts: List<Cast>)

    @Query("DELETE FROM casts")
    suspend fun deleteAllCasts()
}