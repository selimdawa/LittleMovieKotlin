package com.flatcode.littlemovieadmin.ui.editorschoice

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
class EditorsChoiceAddViewModel @Inject constructor(
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
                it.name.contains(query, ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _orderBy = MutableStateFlow(DATA.TIMESTAMP)
    private val _isFavoritesMode = MutableStateFlow(false)

    var editorsChoiceId: String? = null
    var oldId: String? = null

    fun init(editorsChoiceId: String?, oldId: String?) {
        this.editorsChoiceId = editorsChoiceId
        this.oldId = oldId
    }

    init {
        fetchData()
    }

    private fun fetchData() {
        val uid = authRepo.getCurrentUserUid()
        viewModelScope.launch {
            combine(_orderBy, _isFavoritesMode) { order, isFav ->
                Pair(order, isFav)
            }.collectLatest { (order, isFav) ->
                _isLoading.value = true
                if (isFav && uid != null) {
                    movieRepo.getFavorites(uid, order).collectLatest { list ->
                        _movies.value = list.filter { it.editorsChoice == 0 }
                        _isLoading.value = false
                    }
                } else {
                    movieRepo.getMovies(order).collectLatest { list ->
                        _movies.value = list.filter { it.editorsChoice == 0 }
                        _isLoading.value = false
                    }
                }
            }
        }
    }

    fun getData(orderBy: String) {
        _isFavoritesMode.value = false
        _orderBy.value = orderBy
    }

    fun getFavorites(orderBy: String) {
        _isFavoritesMode.value = true
        _orderBy.value = orderBy
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}