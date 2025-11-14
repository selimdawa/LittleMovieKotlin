package com.flatcode.littlemovie.Model

import com.flatcode.littlemovie.Unit.DATA

class Cast {
    var id: String? = null
    var publisher: String? = null
    var name: String? = null
    var image: String? = null
    var aboutMy: String? = null
    var interestedCount = 0
    var moviesCount = 0
    var timestamp: Long = 0

    constructor()

    constructor(
        id: String?, publisher: String?, name: String, image: String?, aboutMy: String?,
        timestamp: Long, interestedCount: Int, moviesCount: Int
    ) {
        var name = name
        if (name.trim { it <= ' ' } == DATA.EMPTY) {
            name = "No Name"
        }
        this.id = id
        this.publisher = publisher
        this.name = name
        this.image = image
        this.aboutMy = aboutMy
        this.timestamp = timestamp
        this.interestedCount = interestedCount
        this.moviesCount = moviesCount
    }
}