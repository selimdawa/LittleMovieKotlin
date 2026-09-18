package com.flatcode.littlemovieadmin.ui.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.User
import com.flatcode.littlemovieadmin.repository.UserRepository
import com.flatcode.littlemovieadmin.utils.DATA
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ProfileEditViewModel @Inject constructor(
    private val repository: UserRepository,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileEditUiState())
    val uiState: StateFlow<ProfileEditUiState> = _uiState.asStateFlow()

    init {
        loadUserInfo()
    }

    private fun loadUserInfo() {
        viewModelScope.launch {
            try {
                val user = repository.getUserInfo(DATA.FirebaseUserUid)
                _uiState.update { it.copy(user = user) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading user info")
            }
        }
    }

    fun updateProfile(username: String, imageUri: Uri?, extension: String?, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val imageUrl = if (imageUri != null) {
                    val path = "Images/Profile/${DATA.FirebaseUserUid}.${extension ?: "jpg"}"
                    val ref = storage.getReference(path)
                    ref.putFile(imageUri).await()
                    ref.downloadUrl.await().toString()
                } else null

                val updates = mutableMapOf<String, Any>()
                updates[DATA.USER_NAME] = username
                imageUrl?.let { updates[DATA.PROFILE_IMAGE] = it }

                repository.updateProfile(DATA.FirebaseUserUid, updates)
                _uiState.update { it.copy(isLoading = false) }
                onResult(true, "Profile updated...")
            } catch (e: Exception) {
                Timber.e(e, "Error updating profile")
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, e.message)
            }
        }
    }
}

data class ProfileEditUiState(
    val user: User? = null,
    val isLoading: Boolean = false
)
