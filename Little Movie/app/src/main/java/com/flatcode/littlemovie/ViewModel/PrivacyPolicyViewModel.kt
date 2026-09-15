package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Repository.ToolsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PrivacyPolicyViewModel : ViewModel() {

    private val repository = ToolsRepository()

    private val _privacyPolicy = MutableStateFlow<String?>(null)
    val privacyPolicy: StateFlow<String?> = _privacyPolicy

    fun loadPrivacyPolicy() {
        viewModelScope.launch {
            repository.getPrivacyPolicy().collectLatest {
                _privacyPolicy.value = it
            }
        }
    }
}
