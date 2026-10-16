package com.flatcode.littlemovie.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.repository.CategoryRepository
import com.flatcode.littlemovie.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyCategoriesViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadCategories(orderBy: String) {
        val userId = DATA.FirebaseUserUid ?: return
        if (_categories.value.isNotEmpty()) {
            _categories.value = sortCategories(_categories.value, orderBy)
            return
        }
        _isLoading.value = true
        viewModelScope.launch {
            repository.getInterestedCategories(userId, orderBy).collectLatest { list ->
                val sorted = sortCategories(list, orderBy)
                _categories.value = sorted
                _isLoading.value = false
                Timber.d("My categories updated: %d", sorted.size)
            }
        }
    }

    private fun sortCategories(list: List<Category>, type: String): List<Category> {
        return when (type) {
            DATA.INTERESTED_COUNT -> list.sortedByDescending { it.interestedCount }
            DATA.MOVIES_COUNT -> list.sortedByDescending { it.moviesCount }
            DATA.NAME -> list.sortedBy { it.name?.lowercase() ?: "" }
            else -> list.sortedByDescending { it.timestamp }
        }
    }
}
