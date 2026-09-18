package com.flatcode.littlemovieadmin.model

class Main {
    var title: String? = null
    var image = 0
    var number = 0
    var c: Class<*>? = null

    constructor()

    constructor(image: Int, title: String?, number: Int, c: Class<*>?) {
        this.image = image
        this.number = number
        this.title = title
        this.c = c
    }
}
