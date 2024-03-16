package com.flatcode.littlemovie.Model

import com.flatcode.littlemovie.Unit.DATA

class Movie {
    var id: String? = null
    var publisher: String? = null
    var image: String? = null
    var categoryId: String? = null
    var name: String? = null
    var description: String? = null
    var movieLink: String? = null
    var duration: String? = null
    var viewsCount = 0
    var lovesCount = 0
    var castCount = 0
    var editorsChoice = 0
    var year = 0
    var timestamp: Long = 0

    constructor()

    constructor(
        id: String?, publisher: String?, timestamp: Long, categoryId: String?, name: String,
        description: String?, duration: String?, image: String?, movieLink: String?,
        viewsCount: Int, lovesCount: Int, castCount: Int, editorsChoice: Int, year: Int
    ) {
        var name = name
        if (name.trim { it <= ' ' } == DATA.EMPTY) {
            name = "No Name"
        }
        this.id = id
        this.publisher = publisher
        this.timestamp = timestamp
        this.categoryId = categoryId
        this.name = name
        this.description = description
        this.image = image
        this.duration = duration
        this.movieLink = movieLink
        this.viewsCount = viewsCount
        this.lovesCount = lovesCount
        this.castCount = castCount
        this.editorsChoice = editorsChoice
        this.year = year
    }
}