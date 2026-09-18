package com.flatcode.littlemovie.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.flatcode.littlemovie.utils.DATA
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "casts")
data class Cast(
    @PrimaryKey
    var id: String = "",
    var publisher: String? = null,
    var name: String? = null,
    var image: String? = null,
    var aboutMy: String? = null,
    var interestedCount: Int = 0,
    var moviesCount: Int = 0,
    var timestamp: Long = 0
) : Parcelable
