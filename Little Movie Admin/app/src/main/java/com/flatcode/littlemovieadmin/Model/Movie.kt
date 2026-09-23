package com.flatcode.littlemovieadmin.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Entity(tableName = "movies")
@Parcelize
data class Movie(
    @PrimaryKey var id: String = "",
    var publisher: String? = null,
    var image: String? = null,
    var categoryId: String? = null,
    var name: String = "No Name",
    var description: String? = null,
    var movieLink: String? = null,
    var duration: String? = null,
    var viewsCount: Int = 0,
    var lovesCount: Int = 0,
    var castCount: Int = 0,
    var editorsChoice: Int = 0,
    var year: Int = 0,
    var timestamp: Long = 0,
) : Parcelable