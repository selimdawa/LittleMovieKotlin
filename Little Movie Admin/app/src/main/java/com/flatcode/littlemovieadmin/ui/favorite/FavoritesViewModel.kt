package com.flatcode.littlemovieadmin.ui.favorite

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.Movie
import com.flatcode.littlemovieadmin.repository.AuthRepository
import com.flatcode.littlemovieadmin.repository.MovieRepository
import com.flatcode.littlemovieadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val movieRepo: MovieRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<List<Movie>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    val movies: StateFlow<List<Movie>> = combine(_movies, _searchQuery) { list, query ->
        if (query.isEmpty()) {
            list
        } else {
            list.filter {
                it.name?.contains(query, ignoreCase = true) == true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _orderBy = MutableStateFlow(DATA.TIMESTAMP)
    val orderBy: StateFlow<String> = _orderBy.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        val uid = authRepo.getCurrentUserUid() ?: return
        viewModelScope.launch {
            _orderBy.collectLatest { order ->
                _isLoading.value = true
                movieRepo.getFavorites(uid, order).collectLatest { list ->
                    _movies.value = list
                    _isLoading.value = false
                }
            }
        }
    }

    fun getData(orderBy: String) {
        _orderBy.value = orderBy
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
