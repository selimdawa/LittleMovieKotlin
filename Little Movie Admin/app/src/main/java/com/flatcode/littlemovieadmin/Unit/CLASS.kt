package com.flatcode.littlemovieadmin.Unit

import com.flatcode.littlemovieadmin.Activity.*
import com.flatcode.littlemovieadmin.Activityimport.CastActivity
import com.flatcode.littlemovieadmin.Activityimport.CastAddActivity
import com.flatcode.littlemovieadmin.Activityimport.EditorsChoiceActivity
import com.flatcode.littlemovieadmin.Activityimport.PrivacyPolicyEditActivity
import com.flatcode.littlemovieadmin.Auth.ForgetPasswordActivity
import com.flatcode.littlemovieadmin.Auth.LoginActivity
import com.flatcode.littlemovieadmin.Service.FloatingWidgetService

object CLASS {
    var MAIN: Class<*> = MainActivity::class.java
    var SPLASH: Class<*> = SplashActivity::class.java
    var LOGIN: Class<*> = LoginActivity::class.java
    var FORGET_PASSWORD: Class<*> = ForgetPasswordActivity::class.java
    var FAVORITES: Class<*> = FavoritesActivity::class.java
    var PRIVACY_POLICY: Class<*> = PrivacyPolicyActivity::class.java
    var PRIVACY_POLICY_EDIT: Class<*> = PrivacyPolicyEditActivity::class.java
    var EDITORS_CHOICE: Class<*> = EditorsChoiceActivity::class.java
    var EDITORS_CHOICE_ADD: Class<*> = EditorsChoiceAddActivity::class.java
    var PROFILE: Class<*> = ProfileActivity::class.java
    var PROFILE_EDIT: Class<*> = ProfileEditActivity::class.java
    var USERS: Class<*> = UsersActivity::class.java
    var CATEGORY_ADD: Class<*> = CategoryAddActivity::class.java
    var CATEGORY_EDIT: Class<*> = CategoryEditActivity::class.java
    var CATEGORIES: Class<*> = CategoriesActivity::class.java
    var CATEGORY_DETAILS: Class<*> = CategoryDetailsActivity::class.java
    var CAST_ADD: Class<*> = CastAddActivity::class.java
    var CAST_EDIT: Class<*> = CastEditActivity::class.java
    var CAST: Class<*> = CastActivity::class.java
    var CAST_DETAILS: Class<*> = CastDetailsActivity::class.java
    var SLIDER_SHOW: Class<*> = SliderShowActivity::class.java
    var MOVIE_ADD: Class<*> = MovieAddActivity::class.java
    var MOVIE_EDIT: Class<*> = MovieEditActivity::class.java
    var MOVIE_DETAILS: Class<*> = MovieDetailsActivity::class.java
    var MOVIES: Class<*> = MoviesActivity::class.java
    var MOVIE_VIEW: Class<*> = MovieViewActivity::class.java
    var CAST_MOVIE: Class<*> = CastMovieAddActivity::class.java
    var SERVICE: Class<*> = FloatingWidgetService::class.java
}