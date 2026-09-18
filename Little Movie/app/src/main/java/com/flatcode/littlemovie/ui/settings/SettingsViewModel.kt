package com.flatcode.littlemovie.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.model.User
import com.flatcode.littlemovie.repository.UserRepository
import com.flatcode.littlemovie.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _castCount = MutableStateFlow(0)
    val castCount: StateFlow<Int> = _castCount

    private val _categoriesCount = MutableStateFlow(0)
    val categoriesCount: StateFlow<Int> = _categoriesCount

    private val _favoritesCount = MutableStateFlow(0)
    val favoritesCount: StateFlow<Int> = _favoritesCount

    fun loadData() {
        val userId = DATA.FirebaseUserUid ?: return
        
        viewModelScope.launch {
            repository.getUserInfo(userId).collectLatest {
                _user.value = it
            }
        }

        viewModelScope.launch {
            repository.getInterestedCount(userId, DATA.CAST).collectLatest {
                _castCount.value = it
            }
        }

        viewModelScope.launch {
            repository.getInterestedCount(userId, DATA.CATEGORIES).collectLatest {
                _categoriesCount.value = it
            }
        }

        viewModelScope.launch {
            repository.getFavoritesCount(userId).collectLatest {
                _favoritesCount.value = it
            }
        }
    }
}
