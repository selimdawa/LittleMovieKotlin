package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Model.Category
import com.flatcode.littlemovie.Model.Movie
import com.flatcode.littlemovie.Repository.CategoryRepository
import com.flatcode.littlemovie.Repository.MovieRepository
import com.flatcode.littlemovie.Unit.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _editorsChoiceMovies = MutableStateFlow<List<Movie>>(emptyList())
    val editorsChoiceMovies: StateFlow<List<Movie>> = _editorsChoiceMovies

    private val _mostViewedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val mostViewedMovies: StateFlow<List<Movie>> = _mostViewedMovies

    private val _mostLovedMovies = MutableStateFlow<List<Movie>>(emptyList())
    val mostLovedMovies: StateFlow<List<Movie>> = _mostLovedMovies

    private val _newMovies = MutableStateFlow<List<Movie>>(emptyList())
    val newMovies: StateFlow<List<Movie>> = _newMovies

    private val _sliderCount = MutableStateFlow(0)
    val sliderCount: StateFlow<Int> = _sliderCount

    fun loadData() {
        loadCategories()
        loadSliderShow()
        loadMovies(DATA.EDITORS_CHOICE, _editorsChoiceMovies)
        loadMovies(DATA.VIEWS_COUNT, _mostViewedMovies)
        loadMovies(DATA.LOVES_COUNT, _mostLovedMovies)
        loadMovies(DATA.TIMESTAMP, _newMovies)
    }

    private fun loadCategories() {
        viewModelScope.launch {
            categoryRepository.getCategories().collectLatest { list ->
                _categories.value = list
                Timber.d("Categories updated: %d", list.size)
            }
        }
    }

    private fun loadSliderShow() {
        viewModelScope.launch {
            movieRepository.getSliderCount().collectLatest { count ->
                _sliderCount.value = count
                Timber.d("Slider count updated: %d", count)
            }
        }
    }

    private fun loadMovies(orderBy: String, stateFlow: MutableStateFlow<List<Movie>>) {
        viewModelScope.launch {
            movieRepository.getMovies(orderBy).collectLatest { list ->
                stateFlow.value = list
                Timber.d("Movies updated for %s: %d", orderBy, list.size)
            }
        }
    }
}
