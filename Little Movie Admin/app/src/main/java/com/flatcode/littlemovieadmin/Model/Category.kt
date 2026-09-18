package com.flatcode.littlemovieadmin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "categories")
@Parcelize
data class Category(
    @PrimaryKey
    var id: String = "",
    var name: String? = null,
    var image: String? = null,
    var publisher: String? = null,
    var interestedCount: Int = 0,
    var moviesCount: Int = 0,
    var timestamp: Long = 0
) : Parcelable {
    constructor() : this("")

    constructor(
        id: String?, name: String?, image: String?, publisher: String?, timestamp: Long,
        interestedCount: Int, moviesCount: Int
    ) : this(
        id ?: "",
        name,
        image,
        publisher,
        interestedCount,
        moviesCount,
        timestamp
    )
}
