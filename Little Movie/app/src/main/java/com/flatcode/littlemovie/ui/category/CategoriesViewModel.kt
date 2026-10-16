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
class CategoriesViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _categoriesList = MutableStateFlow<List<Category>>(emptyList())
    val categoriesList: StateFlow<List<Category>> = _categoriesList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _categoriesCount = MutableStateFlow(0)
    val categoriesCount: StateFlow<Int> = _categoriesCount

    fun setType(type: String) {
        if (_categoriesList.value.isNotEmpty()) {
            _categoriesList.value = sortCategories(_categoriesList.value, type)
        }
    }

    fun getData(orderBy: String? = null, publisherId: String? = null) {
        if (_categoriesList.value.isEmpty()) {
            _isLoading.value = true
        }
        viewModelScope.launch {
            repository.getCategories(publisherId).collectLatest { list ->
                val sorted = sortCategories(list, orderBy ?: DATA.TIMESTAMP)
                _categoriesList.value = sorted
                _categoriesCount.value = sorted.size
                _isLoading.value = false
                Timber.d("Categories updated: %d", sorted.size)
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
