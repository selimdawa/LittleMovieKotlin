package com.flatcode.littlemovie.di

import android.content.Context
import androidx.room.Room
import com.flatcode.littlemovie.db.AppDatabase
import com.flatcode.littlemovie.db.CastDao
import com.flatcode.littlemovie.db.CategoryDao
import com.flatcode.littlemovie.db.CommentDao
import com.flatcode.littlemovie.db.EditorsChoiceDao
import com.flatcode.littlemovie.db.FavoriteDao
import com.flatcode.littlemovie.db.InterestedDao
import com.flatcode.littlemovie.db.MovieDao
import com.flatcode.littlemovie.db.SliderDao
import com.flatcode.littlemovie.db.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "little_movie_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideMovieDao(database: AppDatabase): MovieDao = database.movieDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()

    @Provides
    fun provideCastDao(database: AppDatabase): CastDao = database.castDao()

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()

    @Provides
    fun provideCommentDao(database: AppDatabase): CommentDao = database.commentDao()

    @Provides
    fun provideEditorsChoiceDao(database: AppDatabase): EditorsChoiceDao = database.editorsChoiceDao()

    @Provides
    fun provideFavoriteDao(database: AppDatabase): FavoriteDao = database.favoriteDao()

    @Provides
    fun provideInterestedDao(database: AppDatabase): InterestedDao = database.interestedDao()

    @Provides
    fun provideSliderDao(database: AppDatabase): SliderDao = database.sliderDao()
}