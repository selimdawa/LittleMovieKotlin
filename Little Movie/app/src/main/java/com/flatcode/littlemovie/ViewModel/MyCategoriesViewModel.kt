package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Model.Category
import com.flatcode.littlemovie.Repository.CategoryRepository
import com.flatcode.littlemovie.Unit.DATA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class MyCategoriesViewModel : ViewModel() {

    private val repository = CategoryRepository()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadCategories(orderBy: String) {
        val userId = DATA.FirebaseUserUid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            repository.getInterestedCategories(userId, orderBy).collectLatest { list ->
                _categories.value = list
                _isLoading.value = false
                Timber.d("My categories updated: %d", list.size)
            }
        }
    }
}
