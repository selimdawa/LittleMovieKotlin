package com.flatcode.littlemovieadmin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.Model.Main
import com.flatcode.littlemovieadmin.R
import com.flatcode.littlemovieadmin.Repository.*
import com.flatcode.littlemovieadmin.Unit.CLASS
import com.flatcode.littlemovieadmin.Unit.DATA
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
                    add(Main(R.drawable.ic_person, "Users", users, CLASS.USERS))
                    add(Main(R.drawable.ic_add, "Add Movie", 0, CLASS.MOVIE_ADD))
                    add(Main(R.drawable.ic_movie, "Movies", moviesCount, CLASS.MOVIES))
                    add(Main(R.drawable.ic_users, "Editors Choice", editorsChoiceCount, CLASS.EDITORS_CHOICE))
                    add(Main(R.drawable.ic_add_category, "Add Category", 0, CLASS.CATEGORY_ADD))
                    add(Main(R.drawable.ic_category_gray, "Categories", categoriesCount, CLASS.CATEGORIES))
                    add(Main(R.drawable.ic_slider, "Slider Show", sliderCount, CLASS.SLIDER_SHOW))
                    add(Main(R.drawable.ic__add, "Add Cast", 0, CLASS.CAST_ADD))
                    add(Main(R.drawable.ic_cast, "Cast", castCount, CLASS.CAST))
                    add(Main(R.drawable.ic_star_selected, "Favorites", favoritesCount, CLASS.FAVORITES))
                    add(Main(R.drawable.ic_privacy_policy, "Privacy Policy", 0, CLASS.PRIVACY_POLICY))
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
