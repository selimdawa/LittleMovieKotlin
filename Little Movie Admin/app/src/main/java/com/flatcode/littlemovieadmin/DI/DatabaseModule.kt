package com.flatcode.littlemovieadmin.di

import android.content.Context
import androidx.room.Room
import com.flatcode.littlemovieadmin.db.AppDatabase
import com.flatcode.littlemovieadmin.db.CastDao
import com.flatcode.littlemovieadmin.db.CategoryDao
import com.flatcode.littlemovieadmin.db.MovieDao
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
            context,
            AppDatabase::class.java,
            "little_movie_db"
        ).build()
    }

    @Provides
    fun provideMovieDao(database: AppDatabase): MovieDao = database.movieDao()

    @Provides
    fun provideCastDao(database: AppDatabase): CastDao = database.castDao()

    @Provides
    fun provideCategoryDao(database: AppDatabase): CategoryDao = database.categoryDao()
}
