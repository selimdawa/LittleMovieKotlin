package com.flatcode.littlemovie.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlemovie.model.Cast
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.model.InterestedEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InterestedDao {

    @Query("SELECT categories.* FROM categories INNER JOIN interested ON categories.id = interested.itemId WHERE interested.userId = :userId AND interested.databaseName = :databaseName ORDER BY categories.timestamp DESC")
    fun getInterestedCategories(userId: String, databaseName: String): Flow<List<Category>>

    @Query("SELECT casts.* FROM casts INNER JOIN interested ON casts.id = interested.itemId WHERE interested.userId = :userId AND interested.databaseName = :databaseName ORDER BY casts.timestamp DESC")
    fun getInterestedCasts(userId: String, databaseName: String): Flow<List<Cast>>

    @Query("SELECT COUNT(*) FROM interested WHERE userId = :userId AND databaseName = :databaseName")
    fun getInterestedCount(userId: String, databaseName: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterested(interested: InterestedEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterestedList(list: List<InterestedEntity>)

    @Query("DELETE FROM interested WHERE userId = :userId AND databaseName = :databaseName AND itemId = :itemId")
    suspend fun deleteInterested(userId: String, databaseName: String, itemId: String)

    @Query("DELETE FROM interested WHERE userId = :userId AND databaseName = :databaseName")
    suspend fun deleteAllInterestedForUser(userId: String, databaseName: String)
}