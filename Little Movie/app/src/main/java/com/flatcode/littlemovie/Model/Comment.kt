package com.flatcode.littlemovie.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Comment(
    var id: String? = null,
    var movieId: String? = null,
    var comment: String? = null,
    var publisher: String? = null,
    var timestamp: Long = 0,
) : Parcelable