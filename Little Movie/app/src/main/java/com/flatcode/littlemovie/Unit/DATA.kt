package com.flatcode.littlemovie.Unit

import com.google.firebase.auth.FirebaseAuth

object DATA {
    var USERS = "Users"
    var TOOLS = "Tools"
    var EMAIL = "email"
    var CATEGORIES = "Categories"
    var CAST = "Cast"
    var MOVIES = "Movies"
    var INTERESTED = "Interested"
    var LOVES = "Loves"
    var VERSION = "version"
    var PRIVACY_POLICY = "privacyPolicy"
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
    var COMMENTS = "Comments"
    var NULL = "null"
    var FAVORITES = "Favorites"
    var VIEWS_COUNT = "viewsCount"
    var INTERESTED_COUNT = "interestedCount"
    var MOVIES_COUNT = "moviesCount"
    var LOVES_COUNT = "lovesCount"
    var EDITORS_CHOICE = "editorsChoice"
    var NAME = "name"
    var CAST_MOVIE = "CastMovie"
    var MOVIE_LINK = "movieLink"
    var DOT = "."
    var CURRENT_VERSION = 1
    var MIX_SQUARE = 500
    var ZERO = 0
    var ORDER_MAIN = 2 // Here Max Item Show
    var searchStatus = false
    var isChange = false

    //Shared
    var PROFILE_ID = "profileId"
    var COLOR_OPTION = "color_option"
    var CATEGORY_ID = "categoryId"
    var SHOW_MORE_TYPE = "showMoreType"
    var CATEGORY_NAME = "categoryName"
    var SHOW_MORE_NAME = "showMoreName"
    var SHOW_MORE_BOOLEAN = "showMoreBoolean"
    var FB_ID = ""
    var WEB_SITE = ""
    var CAST_ID = "castId"
    var CAST_NAME = "castName"
    var CAST_ABOUT = "castAbout"
    var CAST_IMAGE = "castImage"
    var MOVIE_ID = "movieId"
    var COMMENT = "comment"

    //Other
    val AUTH = FirebaseAuth.getInstance()
    val FIREBASE_USER = AUTH.currentUser
    val FirebaseUserUid = FIREBASE_USER!!.uid
}