package com.flatcode.littlemovie.Model

class Comment {
    var id: String? = null
    var movieId: String? = null
    var comment: String? = null
    var publisher: String? = null
    var timestamp: Long = 0

    constructor()

    constructor(
        id: String?, movieId: String?, timestamp: Long, comment: String?, publisher: String?
    ) {
        this.id = id
        this.movieId = movieId
        this.timestamp = timestamp
        this.comment = comment
        this.publisher = publisher
    }
}