package com.flatcode.littlemovie.utils

import com.flatcode.littlemovie.ui.main.MainActivity
import com.flatcode.littlemovie.ui.main.SplashActivity
import com.flatcode.littlemovie.ui.auth.AuthActivity
import com.flatcode.littlemovie.ui.auth.ForgetPasswordActivity
import com.flatcode.littlemovie.ui.auth.LoginActivity
import com.flatcode.littlemovie.ui.auth.RegisterActivity
import com.flatcode.littlemovie.ui.category.CategoriesActivity
import com.flatcode.littlemovie.ui.category.CategoryDetailsActivity
import com.flatcode.littlemovie.ui.category.MyCategoriesActivity
import com.flatcode.littlemovie.ui.cast.CastActivity
import com.flatcode.littlemovie.ui.cast.CastDetailsActivity
import com.flatcode.littlemovie.ui.cast.MyCastActivity
import com.flatcode.littlemovie.ui.profile.ProfileActivity
import com.flatcode.littlemovie.ui.profile.ProfileEditActivity
import com.flatcode.littlemovie.ui.profile.FavoritesActivity
import com.flatcode.littlemovie.ui.movie.MovieDetailsActivity
import com.flatcode.littlemovie.ui.movie.MovieViewActivity
import com.flatcode.littlemovie.ui.movie.ShowMoreActivity
import com.flatcode.littlemovie.ui.settings.PrivacyPolicyActivity
import com.flatcode.littlemovie.service.FloatingWidgetService

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
