package com.flatcode.littlemovie.model

import androidx.room.Entity

@Entity(tableName = "favorites", primaryKeys = ["userId", "movieId"])
data class FavoriteEntity(
    val userId: String = "",
    val movieId: String = ""
)
