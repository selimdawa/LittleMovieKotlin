package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Model.Category
import com.flatcode.littlemovie.Repository.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class CategoriesViewModel : ViewModel() {

    private val repository = CategoryRepository()

    private val _categoriesList = MutableStateFlow<List<Category>>(emptyList())
    val categoriesList: StateFlow<List<Category>> = _categoriesList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _categoriesCount = MutableStateFlow(0)
    val categoriesCount: StateFlow<Int> = _categoriesCount

    fun getData(orderBy: String? = null, publisherId: String? = null) {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getCategories(publisherId).collectLatest { list ->
                // Basic filtering/sorting if needed in VM
                val processedList = if (orderBy != null) {
                    list.sortedByDescending { it.name } // Example sorting
                } else {
                    list.reversed()
                }
                
                _categoriesList.value = processedList
                _categoriesCount.value = processedList.size
                _isLoading.value = false
                Timber.d("Categories updated: %d", processedList.size)
            }
        }
    }
}
