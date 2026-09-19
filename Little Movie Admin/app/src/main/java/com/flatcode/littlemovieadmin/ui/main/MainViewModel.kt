package com.flatcode.littlemovieadmin.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.Main
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.repository.*
import com.flatcode.littlemovieadmin.ui.cast.CastActivity
import com.flatcode.littlemovieadmin.ui.cast.CastAddActivity
import com.flatcode.littlemovieadmin.ui.category.CategoriesActivity
import com.flatcode.littlemovieadmin.ui.category.CategoryAddActivity
import com.flatcode.littlemovieadmin.ui.editorschoice.EditorsChoiceActivity
import com.flatcode.littlemovieadmin.ui.favorite.FavoritesActivity
import com.flatcode.littlemovieadmin.ui.movie.MovieAddActivity
import com.flatcode.littlemovieadmin.ui.movie.MoviesActivity
import com.flatcode.littlemovieadmin.ui.privacypolicy.PrivacyPolicyActivity
import com.flatcode.littlemovieadmin.ui.slider.SliderShowActivity
import com.flatcode.littlemovieadmin.ui.users.UsersActivity
import com.flatcode.littlemovieadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val authRepo: AuthRepository,
    private val userRepo: UserRepository,
    private val movieRepo: MovieRepository,
    private val categoryRepo: CategoryRepository,
    private val castRepo: CastRepository,
    private val sliderRepo: SliderRepository,
    private val privacyRepo: PrivacyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val uid = authRepo.getCurrentUserUid() ?: return@launch
                val user = userRepo.getUserInfo(uid)
                
                // Fetching all counts in parallel could be better, but let's do sequential for now or use async
                // For simplicity and to avoid too many listeners, repositories use .get().await()
                
                val users = userRepo.getAllUsers().count { it.id != uid }
                val movies = movieRepo.getMovies(DATA.TIMESTAMP)
                val moviesCount = movies.size
                val editorsChoiceCount = movies.count { it.editorsChoice != 0 && it.publisher == uid }
                
                val categoriesCount = categoryRepo.getCategories(DATA.TIMESTAMP).count { it.publisher == uid }
                val sliderCount = sliderRepo.getSliderImages().filter { it.value.isNotEmpty() }.size
                val castCount = castRepo.getCastList(DATA.TIMESTAMP).size
                val favoritesCount = movieRepo.getFavoriteMovieIds(uid).size

                val list = mutableListOf<Main>().apply {
                    add(Main(R.drawable.ic_person, "Users", users, UsersActivity::class.java))
                    add(Main(R.drawable.ic_add, "Add Movie", 0, MovieAddActivity::class.java))
                    add(Main(R.drawable.ic_movie, "Movies", moviesCount, MoviesActivity::class.java))
                    add(Main(R.drawable.ic_users, "Editors Choice", editorsChoiceCount, EditorsChoiceActivity::class.java))
                    add(Main(R.drawable.ic_add_category, "Add Category", 0, CategoryAddActivity::class.java))
                    add(Main(R.drawable.ic_category_gray, "Categories", categoriesCount, CategoriesActivity::class.java))
                    add(Main(R.drawable.ic_slider, "Slider Show", sliderCount, SliderShowActivity::class.java))
                    add(Main(R.drawable.ic__add, "Add Cast", 0, CastAddActivity::class.java))
                    add(Main(R.drawable.ic_cast, "Cast", castCount, CastActivity::class.java))
                    add(Main(R.drawable.ic_star_selected, "Favorites", favoritesCount, FavoritesActivity::class.java))
                    add(Main(R.drawable.ic_privacy_policy, "Privacy Policy", 0, PrivacyPolicyActivity::class.java))
                }

                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        items = list, 
                        userProfileImage = user?.profileImage 
                    ) 
                }
            } catch (e: Exception) {
                Timber.e(e, "Error refreshing Main data")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class MainUiState(
    val isLoading: Boolean = false,
    val items: List<Main> = emptyList(),
    val userProfileImage: String? = null
)
