@file:Suppress("SpellCheckingInspection")

package com.flatcode.littlemovie.utils

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
    var TIMESTAMP = "timestamp"
    var ID = "id"
    var IMAGE = "image"
    var SLIDER_SHOW = "SliderShow"
    var PUBLISHER = "publisher"
    var COMMENTS = "Comments"
    var FAVORITES = "Favorites"
    var VIEWS_COUNT = "viewsCount"
    var INTERESTED_COUNT = "interestedCount"
    var MOVIES_COUNT = "moviesCount"
    var LOVES_COUNT = "lovesCount"
    var EDITORS_CHOICE = "editorsChoice"
    var NAME = "name"
    var CAST_MOVIE = "CastMovie"
    var MOVIE_LINK = "movieLink"
    var CURRENT_VERSION = 1
    var MIX_SQUARE = 500
    var searchStatus = false
    var isChange = false

    //Shared
    var PROFILE_ID = "profileId"
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

    // Cloudinary
    var CLOUDINARY_CLOUD_NAME = "j8jsphcf"
    var CLOUDINARY_UPLOAD_PRESET = "flat_code"

    //Other
    val AUTH: FirebaseAuth get() = FirebaseAuth.getInstance()
    val FIREBASE_USER get() = AUTH.currentUser
    val FirebaseUserUid: String? get() = FIREBASE_USER?.uid
}
