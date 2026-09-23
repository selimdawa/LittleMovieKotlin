package com.flatcode.littlemovieadmin.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Keep
@Entity(tableName = "casts")
@Parcelize
data class Cast(
    @PrimaryKey var id: String = "",
    var publisher: String? = null,
    var name: String = "No Name",
    var image: String? = null,
    var aboutMy: String? = null,
    var interestedCount: Int = 0,
    var moviesCount: Int = 0,
    var timestamp: Long = 0,
) : Parcelable {

    // Compatibility constructor
    constructor(
        id: String?, name: String, image: String?,
    ) : this(
        id = id ?: "",
        name = name.ifBlank { "No Name" },
        image = image,
    )
}