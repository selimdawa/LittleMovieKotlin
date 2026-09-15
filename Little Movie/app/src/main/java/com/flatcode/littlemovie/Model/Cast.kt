package com.flatcode.littlemovie.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flatcode.littlemovie.Unit.DATA
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "casts")
class Cast : Parcelable {
    @PrimaryKey
    var id: String = ""
    var publisher: String? = null
    var name: String? = null
    var image: String? = null
    var aboutMy: String? = null
    var interestedCount = 0
    var moviesCount = 0
    var timestamp: Long = 0

    constructor()

    constructor(
        id: String?, publisher: String?, name: String, image: String?, aboutMy: String?,
        timestamp: Long, interestedCount: Int, moviesCount: Int
    ) {
        var name = name
        if (name.trim { it <= ' ' } == DATA.EMPTY) {
            name = "No Name"
        }
        this.id = id
        this.publisher = publisher
        this.name = name
        this.image = image
        this.aboutMy = aboutMy
        this.timestamp = timestamp
        this.interestedCount = interestedCount
        this.moviesCount = moviesCount
    }
}