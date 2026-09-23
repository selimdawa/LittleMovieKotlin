package com.flatcode.littlemovieadmin.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlemovieadmin.model.Cast
import kotlinx.coroutines.flow.Flow

@Dao
interface CastDao {
    @Query("SELECT * FROM casts ORDER BY timestamp DESC")
    fun getAllCasts(): Flow<List<Cast>>

    @Query("SELECT * FROM casts WHERE id = :castId")
    suspend fun getCastById(castId: String): Cast?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCasts(casts: List<Cast>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCast(cast: Cast)

    @Delete
    suspend fun deleteCast(cast: Cast)

    @Query("DELETE FROM casts")
    suspend fun deleteAllCasts()
}