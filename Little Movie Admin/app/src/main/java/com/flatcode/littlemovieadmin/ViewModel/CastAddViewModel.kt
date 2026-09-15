package com.flatcode.littlemovieadmin.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.Model.Cast
import com.flatcode.littlemovieadmin.Repository.AuthRepository
import com.flatcode.littlemovieadmin.Repository.CastRepository
import com.flatcode.littlemovieadmin.Unit.DATA
import com.google.firebase.database.FirebaseDatabase
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
class CastAddViewModel @Inject constructor(
    private val repository: CastRepository,
    private val authRepo: AuthRepository,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(CastAddUiState())
    val uiState: StateFlow<CastAddUiState> = _uiState.asStateFlow()

    fun uploadCast(name: String, aboutMy: String, imageUri: Uri, extension: String?, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val ref = FirebaseDatabase.getInstance().getReference(DATA.CAST)
                val id = ref.push().key ?: throw Exception("Database error")
                
                val path = "Images/Cast/$id.${extension ?: "jpg"}"
                val storageRef = storage.getReference(path)
                storageRef.putFile(imageUri).await()
                val imageUrl = storageRef.downloadUrl.await().toString()

                val cast = Cast().apply {
                    this.id = id
                    this.publisher = authRepo.getCurrentUserUid()
                    this.timestamp = System.currentTimeMillis()
                    this.name = name
                    this.aboutMy = aboutMy
                    this.image = imageUrl
                    this.interestedCount = 0
                    this.moviesCount = 0
                }

                repository.addCast(cast, id)
                _uiState.update { it.copy(isLoading = false) }
                onResult(true, "Successfully uploaded...")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, e.message)
            }
        }
    }
}

data class CastAddUiState(
    val isLoading: Boolean = false
)
