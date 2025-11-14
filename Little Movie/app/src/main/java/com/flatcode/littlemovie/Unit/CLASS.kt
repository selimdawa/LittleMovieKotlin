package com.flatcode.littlemovie.Unit

import com.flatcode.littlemovie.Activity.*
import com.flatcode.littlemovie.Auth.AuthActivity
import com.flatcode.littlemovie.Auth.ForgetPasswordActivity
import com.flatcode.littlemovie.Auth.LoginActivity
import com.flatcode.littlemovie.Auth.RegisterActivity
import com.flatcode.littlemovie.Service.FloatingWidgetService

object CLASS {
    var MAIN: Class<*> = MainActivity::class.java
    var SPLASH: Class<*> = SplashActivity::class.java
    var AUTH: Class<*> = AuthActivity::class.java
    var LOGIN: Class<*> = LoginActivity::class.java
    var REGISTER: Class<*> = RegisterActivity::class.java
    var FORGET_PASSWORD: Class<*> = ForgetPasswordActivity::class.java
    var CATEGORY_DETAILS: Class<*> = CategoryDetailsActivity::class.java
    var CATEGORIES: Class<*> = CategoriesActivity::class.java
    var CAST: Class<*> = CastActivity::class.java
    var CAST_DETAILS: Class<*> = CastDetailsActivity::class.java
    var PROFILE: Class<*> = ProfileActivity::class.java
    var PROFILE_EDIT: Class<*> = ProfileEditActivity::class.java
    var FAVORITES: Class<*> = FavoritesActivity::class.java
    var PRIVACY_POLICY: Class<*> = PrivacyPolicyActivity::class.java
    var SHOW_MORE: Class<*> = ShowMoreActivity::class.java
    var MY_CAST: Class<*> = MyCastActivity::class.java
    var MY_CATEGORIES: Class<*> = MyCategoriesActivity::class.java
    var MOVIE_DETAILS: Class<*> = MovieDetailsActivity::class.java
    var MOVIE_VIEW: Class<*> = MovieViewActivity::class.java
    var SERVICE: Class<*> = FloatingWidgetService::class.java
}