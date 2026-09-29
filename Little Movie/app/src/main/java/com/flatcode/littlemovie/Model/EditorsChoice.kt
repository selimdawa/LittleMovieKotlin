package com.flatcode.littlemovie.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "editors_choice")
data class EditorsChoice(
    @PrimaryKey var id: String = "",
    var movieId: String? = null,
    var timestamp: Long = 0,
) : Parcelable