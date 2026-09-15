package com.flatcode.littlemovieadmin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.Repository.PrivacyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PrivacyPolicyViewModel @Inject constructor(
    private val repository: PrivacyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacyPolicyUiState())
    val uiState: StateFlow<PrivacyPolicyUiState> = _uiState.asStateFlow()

    init {
        loadPrivacyPolicy()
    }

    fun loadPrivacyPolicy() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val content = repository.getPrivacyPolicy()
                _uiState.update { it.copy(isLoading = false, content = content) }
            } catch (e: Exception) {
                Timber.e(e, "Error loading privacy policy")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun updatePrivacyPolicy(newContent: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.updatePrivacyPolicy(newContent)
                _uiState.update { it.copy(isLoading = false, content = newContent) }
                onResult(true, "Privacy Policy updated...")
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
                onResult(false, e.message)
            }
        }
    }
}

data class PrivacyPolicyUiState(
    val content: String = "",
    val isLoading: Boolean = false
)
