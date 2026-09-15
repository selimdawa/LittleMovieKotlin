package com.flatcode.littlemovieadmin.Data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flatcode.littlemovieadmin.Data.Dao.CastDao
import com.flatcode.littlemovieadmin.Data.Dao.CategoryDao
import com.flatcode.littlemovieadmin.Data.Dao.MovieDao
import com.flatcode.littlemovieadmin.Model.Cast
import com.flatcode.littlemovieadmin.Model.Category
import com.flatcode.littlemovieadmin.Model.Movie

@Database(entities = [Movie::class, Cast::class, Category::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun castDao(): CastDao
    abstract fun categoryDao(): CategoryDao
}