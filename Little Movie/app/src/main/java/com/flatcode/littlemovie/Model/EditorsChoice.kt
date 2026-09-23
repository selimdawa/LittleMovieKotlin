package com.flatcode.littlemovie.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class EditorsChoice(
    var id: String = "",
    var movieId: String? = null,
    var timestamp: Long = 0,
) : Parcelable