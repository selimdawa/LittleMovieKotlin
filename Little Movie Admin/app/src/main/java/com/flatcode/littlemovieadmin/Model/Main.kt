package com.flatcode.littlemovieadmin.model

import androidx.annotation.Keep

@Keep
data class Main(
    var image: Int = 0,
    var title: String? = null,
    var number: Int = 0,
    var c: Class<*>? = null,
)