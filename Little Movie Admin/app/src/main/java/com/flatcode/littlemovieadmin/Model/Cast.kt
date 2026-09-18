package com.flatcode.littlemovieadmin.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "casts")
@Parcelize
data class Cast(
    @PrimaryKey
    var id: String = "",
    var publisher: String? = null,
    var name: String = "No Name",
    var image: String? = null,
    var aboutMy: String? = null,
    var interestedCount: Int = 0,
    var moviesCount: Int = 0,
    var timestamp: Long = 0
) : Parcelable {
    // No-arg constructor for Firebase
    constructor() : this("")

    // Compatibility constructor for existing code
    constructor(
        id: String?, name: String, image: String?
    ) : this(
        id = id ?: "",
        name = if (name.trim().isEmpty()) "No Name" else name,
        image = image
    )
}
