package com.flatcode.littlemovieadmin.utils

import com.flatcode.littlemovieadmin.ui.main.*
import com.flatcode.littlemovieadmin.ui.cast.CastActivity
import com.flatcode.littlemovieadmin.ui.cast.CastAddActivity
import com.flatcode.littlemovieadmin.ui.editorschoice.EditorsChoiceActivity
import com.flatcode.littlemovieadmin.ui.privacypolicy.PrivacyPolicyEditActivity
import com.flatcode.littlemovieadmin.ui.auth.ForgetPasswordActivity
import com.flatcode.littlemovieadmin.ui.auth.LoginActivity
import com.flatcode.littlemovieadmin.service.FloatingWidgetService
import com.flatcode.littlemovieadmin.ui.favorite.FavoritesActivity
import com.flatcode.littlemovieadmin.ui.privacypolicy.PrivacyPolicyActivity
import com.flatcode.littlemovieadmin.ui.editorschoice.EditorsChoiceAddActivity
import com.flatcode.littlemovieadmin.ui.profile.ProfileActivity
import com.flatcode.littlemovieadmin.ui.profile.ProfileEditActivity
import com.flatcode.littlemovieadmin.ui.users.UsersActivity
import com.flatcode.littlemovieadmin.ui.category.CategoryAddActivity
import com.flatcode.littlemovieadmin.ui.category.CategoryEditActivity
import com.flatcode.littlemovieadmin.ui.category.CategoriesActivity
import com.flatcode.littlemovieadmin.ui.category.CategoryDetailsActivity
import com.flatcode.littlemovieadmin.ui.cast.CastEditActivity
import com.flatcode.littlemovieadmin.ui.cast.CastDetailsActivity
import com.flatcode.littlemovieadmin.ui.slider.SliderShowActivity
import com.flatcode.littlemovieadmin.ui.movie.MovieAddActivity
import com.flatcode.littlemovieadmin.ui.movie.MovieEditActivity
import com.flatcode.littlemovieadmin.ui.movie.MovieDetailsActivity
import com.flatcode.littlemovieadmin.ui.movie.MoviesActivity
import com.flatcode.littlemovieadmin.ui.movie.MovieViewActivity
import com.flatcode.littlemovieadmin.ui.movie.CastMovieAddActivity

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
