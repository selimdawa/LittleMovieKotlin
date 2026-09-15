package com.flatcode.littlemovie.Model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
class User : Parcelable {
    @PrimaryKey
    var id: String = ""
    var username: String? = null
    var profileImage: String? = null
    var email: String? = null
    var timestamp: Long = 0
    var version = 0

    constructor()

    constructor(
        id: String?, username: String?, profileImage: String?, email: String?, timestamp: Long,
        version: Int
    ) {
        this.id = id
        this.username = username
        this.profileImage = profileImage
        this.email = email
        this.timestamp = timestamp
        this.version = version
    }
}