package com.flatcode.littlemovie.ViewModel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Model.User
import com.flatcode.littlemovie.Repository.UserRepository
import com.flatcode.littlemovie.Unit.DATA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

class ProfileEditViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _updateStatus = MutableStateFlow<Result<Unit>?>(null)
    val updateStatus: StateFlow<Result<Unit>?> = _updateStatus

    private val _imageUploadStatus = MutableStateFlow<Result<String>?>(null)
    val imageUploadStatus: StateFlow<Result<String>?> = _imageUploadStatus

    fun loadUserInfo() {
        val userId = DATA.FirebaseUserUid ?: return
        viewModelScope.launch {
            repository.getUserInfo(userId).collectLatest {
                _user.value = it
            }
        }
    }

    fun updateProfile(username: String, imageUri: Uri?, extension: String?) {
        val userId = DATA.FirebaseUserUid ?: return
        viewModelScope.launch {
            if (imageUri != null && extension != null) {
                val uploadResult = repository.uploadProfileImage(userId, imageUri, extension)
                _imageUploadStatus.value = uploadResult
                if (uploadResult.isSuccess) {
                    val imageUrl = uploadResult.getOrNull()
                    val updateResult = repository.updateProfile(userId, username, imageUrl)
                    _updateStatus.value = updateResult
                }
            } else {
                val updateResult = repository.updateProfile(userId, username, null)
                _updateStatus.value = updateResult
            }
        }
    }

    fun resetStatus() {
        _updateStatus.value = null
        _imageUploadStatus.value = null
    }
}
