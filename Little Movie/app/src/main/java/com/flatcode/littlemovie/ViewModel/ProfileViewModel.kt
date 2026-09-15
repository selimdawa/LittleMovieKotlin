package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Model.User
import com.flatcode.littlemovie.Repository.UserRepository
import com.flatcode.littlemovie.Unit.DATA
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _castCount = MutableStateFlow(0)
    val castCount: StateFlow<Int> = _castCount

    private val _categoriesCount = MutableStateFlow(0)
    val categoriesCount: StateFlow<Int> = _categoriesCount

    private val _favoritesCount = MutableStateFlow(0)
    val favoritesCount: StateFlow<Int> = _favoritesCount

    fun loadData(profileId: String) {
        viewModelScope.launch {
            repository.getUserInfo(profileId).collectLatest {
                _user.value = it
            }
        }

        viewModelScope.launch {
            repository.getInterestedCount(profileId, DATA.CAST).collectLatest {
                _castCount.value = it
            }
        }

        viewModelScope.launch {
            repository.getInterestedCount(profileId, DATA.CATEGORIES).collectLatest {
                _categoriesCount.value = it
            }
        }

        viewModelScope.launch {
            repository.getFavoritesCount(profileId).collectLatest {
                _favoritesCount.value = it
            }
        }
    }
}
