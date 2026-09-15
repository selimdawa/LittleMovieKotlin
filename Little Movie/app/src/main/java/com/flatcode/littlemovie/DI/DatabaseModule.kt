package com.flatcode.littlemovie.DI

import android.content.Context
import androidx.room.Room
import com.flatcode.littlemovie.Data.Local.AppDatabase
import com.flatcode.littlemovie.Data.Local.Dao.CastDao
import com.flatcode.littlemovie.Data.Local.Dao.CategoryDao
import com.flatcode.littlemovie.Data.Local.Dao.MovieDao
import com.flatcode.littlemovie.Data.Local.Dao.UserDao
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
}
