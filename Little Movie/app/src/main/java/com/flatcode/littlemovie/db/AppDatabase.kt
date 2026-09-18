package com.flatcode.littlemovie.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.model.Movie
import com.flatcode.littlemovie.model.Cast
import com.flatcode.littlemovie.model.User

@Database(entities = [Movie::class, Category::class, Cast::class, User::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun categoryDao(): CategoryDao
    abstract fun castDao(): CastDao
    abstract fun userDao(): UserDao
}
