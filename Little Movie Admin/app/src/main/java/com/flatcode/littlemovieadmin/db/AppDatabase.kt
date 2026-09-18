package com.flatcode.littlemovieadmin.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flatcode.littlemovieadmin.model.Cast
import com.flatcode.littlemovieadmin.model.Category
import com.flatcode.littlemovieadmin.model.Movie

@Database(entities = [Movie::class, Cast::class, Category::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun castDao(): CastDao
    abstract fun categoryDao(): CategoryDao
}
