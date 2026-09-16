package com.flatcode.littlemovieadmin.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.Model.Category
import com.flatcode.littlemovieadmin.Repository.CategoryRepository
import com.flatcode.littlemovieadmin.Unit.DATA
import com.google.firebase.storage.FirebaseStorage
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
    private val repository: CategoryRepository,
    private val storage: FirebaseStorage
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

    fun updateCategory(name: String, imageUri: Uri?, extension: String?, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val categoryId = _uiState.value.categoryId ?: throw Exception("Invalid Category ID")
                val imageUrl = if (imageUri != null) {
                    val path = "Images/Category/$categoryId.${extension ?: "jpg"}"
                    val ref = storage.getReference(path)
                    ref.putFile(imageUri).await()
                    ref.downloadUrl.await().toString()
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
