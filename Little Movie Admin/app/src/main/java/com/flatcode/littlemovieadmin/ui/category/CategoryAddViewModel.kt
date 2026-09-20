package com.flatcode.littlemovieadmin.ui.category

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.Category
import com.flatcode.littlemovieadmin.repository.AuthRepository
import com.flatcode.littlemovieadmin.repository.CategoryRepository
import com.flatcode.littlemovieadmin.utils.CloudinaryHelper
import com.flatcode.littlemovieadmin.utils.DATA
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class CategoryAddViewModel @Inject constructor(
    private val repository: CategoryRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryAddUiState())
    val uiState: StateFlow<CategoryAddUiState> = _uiState.asStateFlow()

    fun uploadCategory(name: String, imageUri: Uri, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val imageUrl = CloudinaryHelper.uploadFile(imageUri)

                val ref = FirebaseDatabase.getInstance().getReference(DATA.CATEGORIES)
                val id = ref.push().key ?: throw Exception("Database error")

                val category = Category().apply {
                    this.id = id
                    this.publisher = authRepo.getCurrentUserUid()
                    this.timestamp = System.currentTimeMillis()
                    this.name = name
                    this.image = imageUrl
                    this.interestedCount = 0
                    this.moviesCount = 0
                }

                repository.addCategory(category, id)
                _uiState.update { it.copy(isLoading = false) }
                onResult(true, "Successfully uploaded...")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, e.message)
            }
        }
    }
}

data class CategoryAddUiState(
    val isLoading: Boolean = false
)
