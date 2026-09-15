package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel : ViewModel() {

    private val repository = UserRepository()

    private val _registerStatus = MutableStateFlow<Result<Unit>?>(null)
    val registerStatus: StateFlow<Result<Unit>?> = _registerStatus

    fun register(name: String, email: String, password: String) {
        viewModelScope.launch {
            val result = repository.registerUser(name, email, password)
            _registerStatus.value = result
        }
    }

    fun resetStatus() {
        _registerStatus.value = null
    }
}
