package com.flatcode.littlemovie.Model

class Category {
    var id: String? = null
    var name: String? = null
    var image: String? = null
    var publisher: String? = null
    var interestedCount = 0
    var moviesCount = 0
    var timestamp: Long = 0

    constructor()

    constructor(
        id: String?, name: String?, image: String?, publisher: String?, timestamp: Long,
        interestedCount: Int, moviesCount: Int
    ) {
        this.id = id
        this.name = name
        this.publisher = publisher
        this.image = image
        this.timestamp = timestamp
        this.interestedCount = interestedCount
        this.moviesCount = moviesCount
    }
}