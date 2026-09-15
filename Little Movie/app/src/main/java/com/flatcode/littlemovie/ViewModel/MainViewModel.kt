package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Repository.UserRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Objects

class MainViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl: StateFlow<String?> = _profileImageUrl

    fun loadUserInfo() {
        viewModelScope.launch {
            val userId = DATA.FirebaseUserUid
            if (userId != null) {
                repository.getUserProfileImage(userId).collectLatest { imageUrl ->
                    _profileImageUrl.value = imageUrl
                    Timber.d("Profile image updated: %s", imageUrl)
                }
            }
        }
    }
}
