package com.flatcode.littlemovie.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.model.Category
import com.flatcode.littlemovie.repository.CategoryRepository
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
