package com.flatcode.littlemovie.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.flatcode.littlemovie.model.Cast
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.model.Comment
import com.flatcode.littlemovie.model.EditorsChoice
import com.flatcode.littlemovie.model.FavoriteEntity
import com.flatcode.littlemovie.model.InterestedEntity
import com.flatcode.littlemovie.model.Movie
import com.flatcode.littlemovie.model.SliderEntity
import com.flatcode.littlemovie.model.User

@Database(
    entities = [
        Movie::class, Category::class, Cast::class, User::class, Comment::class, EditorsChoice::class,
        FavoriteEntity::class, InterestedEntity::class, SliderEntity::class
    ],
    version = 2,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
    abstract fun categoryDao(): CategoryDao
    abstract fun castDao(): CastDao
    abstract fun userDao(): UserDao
    abstract fun commentDao(): CommentDao
    abstract fun editorsChoiceDao(): EditorsChoiceDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun interestedDao(): InterestedDao
    abstract fun sliderDao(): SliderDao
}