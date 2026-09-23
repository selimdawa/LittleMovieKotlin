package com.flatcode.littlemovieadmin.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize

@Keep
@Parcelize
data class EditorsChoice(
    var id: Int = 0,
) : Parcelable