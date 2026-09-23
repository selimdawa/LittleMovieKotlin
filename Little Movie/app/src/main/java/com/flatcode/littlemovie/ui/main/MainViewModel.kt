package com.flatcode.littlemovie.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.repository.UserRepository
import com.flatcode.littlemovie.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

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