package com.flatcode.littlemovie.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    var id: String = "",
    var name: String? = null,
    var image: String? = null,
    var publisher: String? = null,
    var interestedCount: Int = 0,
    var moviesCount: Int = 0,
    var timestamp: Long = 0
) : Parcelable
