package com.flatcode.littlemovieadmin.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class Comment(
    var id: String? = null,
    var movieId: String? = null,
    var comment: String? = null,
    var publisher: String? = null,
    var timestamp: Long = 0,
) : Parcelable