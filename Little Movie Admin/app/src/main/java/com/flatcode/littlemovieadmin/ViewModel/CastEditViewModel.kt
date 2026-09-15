package com.flatcode.littlemovieadmin.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.Model.Cast
import com.flatcode.littlemovieadmin.Repository.CastRepository
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
class CastEditViewModel @Inject constructor(
    private val repository: CastRepository,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(CastEditUiState())
    val uiState: StateFlow<CastEditUiState> = _uiState.asStateFlow()

    fun init(castId: String) {
        _uiState.update { it.copy(castId = castId) }
        viewModelScope.launch {
            try {
                val cast = repository.getCast(castId)
                _uiState.update { it.copy(cast = cast) }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun updateCast(name: String, aboutMy: String, imageUri: Uri?, extension: String?, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val castId = _uiState.value.castId ?: throw Exception("Invalid Cast ID")
                val imageUrl = if (imageUri != null) {
                    val path = "Images/Cast/$castId.${extension ?: "jpg"}"
                    val ref = storage.getReference(path)
                    ref.putFile(imageUri).await()
                    ref.downloadUrl.await().toString()
                } else null

                val updates = mutableMapOf<String, Any?>()
                updates[DATA.NAME] = name
                updates[DATA.ABOUT_MY] = aboutMy
                imageUrl?.let { updates[DATA.IMAGE] = it }

                repository.updateCast(castId, updates)
                _uiState.update { it.copy(isLoading = false) }
                onResult(true, "Cast updated...")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, e.message)
            }
        }
    }
}

data class CastEditUiState(
    val castId: String? = null,
    val cast: Cast? = null,
    val isLoading: Boolean = false
)
