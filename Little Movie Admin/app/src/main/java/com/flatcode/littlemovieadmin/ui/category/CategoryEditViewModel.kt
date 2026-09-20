package com.flatcode.littlemovieadmin.ui.category

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.Category
import com.flatcode.littlemovieadmin.repository.CategoryRepository
import com.flatcode.littlemovieadmin.utils.CloudinaryHelper
import com.flatcode.littlemovieadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CategoryEditViewModel @Inject constructor(
    private val repository: CategoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryEditUiState())
    val uiState: StateFlow<CategoryEditUiState> = _uiState.asStateFlow()

    fun init(categoryId: String) {
        _uiState.update { it.copy(categoryId = categoryId) }
        viewModelScope.launch {
            try {
                val category = repository.getCategory(categoryId)
                _uiState.update { it.copy(category = category) }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateCategory(name: String, imageUri: Uri?, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val categoryId = _uiState.value.categoryId ?: throw Exception("Invalid Category ID")
                val imageUrl = if (imageUri != null) {
                    CloudinaryHelper.uploadFile(imageUri)
                } else null

                val updates = mutableMapOf<String, Any?>()
                updates[DATA.NAME] = name
                imageUrl?.let { updates[DATA.IMAGE] = it }

                repository.updateCategory(categoryId, updates)
                _uiState.update { it.copy(isLoading = false) }
                onResult(true, "Category updated...")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, e.message)
            }
        }
    }
}

data class CategoryEditUiState(
    val categoryId: String? = null,
    val category: Category? = null,
    val isLoading: Boolean = false
)
