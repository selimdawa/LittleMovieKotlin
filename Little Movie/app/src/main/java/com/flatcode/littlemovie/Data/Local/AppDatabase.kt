package com.flatcode.littlemovie.Data.Local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flatcode.littlemovie.Data.Local.Dao.CategoryDao
import com.flatcode.littlemovie.Data.Local.Dao.MovieDao
import com.flatcode.littlemovie.Model.Category
import com.flatcode.littlemovie.Model.Movie
import com.flatcode.littlemovie.Model.Cast
import com.flatcode.littlemovie.Model.User
import com.flatcode.littlemovie.Data.Local.Dao.CastDao
import com.flatcode.littlemovie.Data.Local.Dao.UserDao

@Database(entities = [Movie::class, Category::class, Cast::class, User::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun categoryDao(): CategoryDao
    abstract fun castDao(): CastDao
    abstract fun userDao(): UserDao
}
