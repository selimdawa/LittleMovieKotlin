package com.flatcode.littlemovieadmin.Unit

import com.google.firebase.auth.FirebaseAuth

object DATA {
    //Database
    var USERS = "Users"
    var TOOLS = "Tools"
    var CATEGORIES = "Categories"
    var CAST = "Cast"
    var MOVIES = "Movies"
    var INTERESTED = "Interested"
    var COMMENTS = "Comments"
    var LOVES = "Loves"
    var PRIVACY_POLICY = "privacyPolicy"
    var DESCRIPTION = "description"
    var BASIC = "basic"
    var USER_NAME = "username"
    var PROFILE_IMAGE = "profileImage"
    var EMPTY = ""
    var SPACE = " "
    var TIMESTAMP = "timestamp"
    var ID = "id"
    var IMAGE = "image"
    var SLIDER_SHOW = "SliderShow"
    var PUBLISHER = "publisher"
    var CATEGORY = "category"
    var ABOUT_MY = "aboutMy"
    var NULL = "null"
    var FAVORITES = "Favorites"
    var VIEWS_COUNT = "viewsCount"
    var CAST_COUNT = "castCount"
    var INTERESTED_COUNT = "interestedCount"
    var MOVIES_COUNT = "moviesCount"
    var LOVES_COUNT = "lovesCount"
    var EDITORS_CHOICE = "editorsChoice"
    var NAME = "name"
    var YEAR = "year"
    var CAST_MOVIE = "CastMovie"
    var DOT = "."
    var castMovie = ArrayList<String?>()
    var castMovieOld = ArrayList<String?>()
    var MIX_SQUARE = 500
    var MIX_VIDEO_X = 400
    var MIX_VIDEO_Y = 560
    var MIX_SLIDER_X = 680
    var MIX_SLIDER_Y = 360
    var ZERO = 0
    var MIN_YEAR = 1940
    var MAX_YEAR = 2022
    var searchStatus = false
    var isChange = false

    //Shared
    var PROFILE_ID = "profileId"
    var COLOR_OPTION = "color_option"
    var EDITORS_CHOICE_ID = "editorsChoiceId"
    var CATEGORY_ID = "categoryId"
    var CAST_ID = "castId"
    var CAST_NAME = "castName"
    var CAST_ABOUT = "castAbout"
    var CAST_IMAGE = "castImage"
    var DURATION = "duration"
    var MOVIE_LINK = "movieLink"
    var OLD_ID = "oldId"
    var CATEGORY_NAME = "categoryName"
    var MOVIE_ID = "movieId"
    var COMMENT = "comment"
    var MOVIE = "Movie"
    var castList: ArrayList<String>? = null
    var movieList: ArrayList<String>? = null

    //Other
    val AUTH = FirebaseAuth.getInstance()
    val FIREBASE_USER = AUTH.currentUser
    val FirebaseUserUid = FIREBASE_USER!!.uid
}