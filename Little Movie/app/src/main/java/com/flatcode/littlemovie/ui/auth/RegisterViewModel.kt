package com.flatcode.littlemovie.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

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
