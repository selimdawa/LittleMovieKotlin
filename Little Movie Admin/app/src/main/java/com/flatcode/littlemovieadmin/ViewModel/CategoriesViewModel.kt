package com.flatcode.littlemovieadmin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.Model.Category
import com.flatcode.littlemovieadmin.Repository.CategoryRepository
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
class CategoriesViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriesUiState())
    val uiState: StateFlow<CategoriesUiState> = _uiState.asStateFlow()

    fun getData(orderBy: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentType = orderBy) }
            try {
                val categories = repository.getCategories(orderBy)
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        categories = categories,
                        count = categories.size
                    ) 
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading categories")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class CategoriesUiState(
    val isLoading: Boolean = false,
    val categories: List<Category> = emptyList(),
    val count: Int = 0,
    val currentType: String = DATA.TIMESTAMP
)
