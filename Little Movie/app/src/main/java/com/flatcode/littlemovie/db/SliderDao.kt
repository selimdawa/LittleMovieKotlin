package com.flatcode.littlemovie.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.flatcode.littlemovie.model.SliderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SliderDao {

    @Query("SELECT * FROM slider ORDER BY position ASC")
    fun getSliderImages(): Flow<List<SliderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSliderImages(images: List<SliderEntity>)

    @Query("DELETE FROM slider")
    suspend fun deleteAllSliderImages()
}
