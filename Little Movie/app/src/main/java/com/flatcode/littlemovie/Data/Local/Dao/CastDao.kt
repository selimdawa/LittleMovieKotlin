package com.flatcode.littlemovie.Data.Local.Dao

import androidx.room.*
import com.flatcode.littlemovie.Model.Cast
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
