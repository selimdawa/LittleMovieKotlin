package com.flatcode.littlemovie.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "categories")
class Category : Parcelable {
    @PrimaryKey
    var id: String = ""
    var name: String? = null
    var image: String? = null
    var publisher: String? = null
    var interestedCount = 0
    var moviesCount = 0
    var timestamp: Long = 0

    constructor()

    constructor(
        id: String?, name: String?, image: String?, publisher: String?, timestamp: Long,
        interestedCount: Int, moviesCount: Int
    ) {
        this.id = id
        this.name = name
        this.publisher = publisher
        this.image = image
        this.timestamp = timestamp
        this.interestedCount = interestedCount
        this.moviesCount = moviesCount
    }
}