package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Repository.UserRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class SplashViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn

    fun checkUser(delayMillis: Long) {
        viewModelScope.launch {
            delay(delayMillis)
            val loggedIn = repository.isUserLoggedIn()
            _isLoggedIn.value = loggedIn
            Timber.d("User login status checked: %b", loggedIn)
        }
    }
}
