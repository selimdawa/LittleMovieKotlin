package com.flatcode.littlemovieadmin.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.User
import com.flatcode.littlemovieadmin.repository.AuthRepository
import com.flatcode.littlemovieadmin.repository.UserRepository
import com.flatcode.littlemovieadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UserRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()

    fun getData(orderBy: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentType = orderBy) }
            try {
                val myUid = authRepo.getCurrentUserUid()
                val allUsers = repository.getAllUsers()
                // Filtering and sorting manually because database query for 'orderBy' is limited
                // but let's assume we use the repository method if we can sort by child.
                // Firebase .get() doesn't support complex sorting as easily as listeners, 
                // but we can sort the list here.
                
                val filteredUsers = allUsers.filter { it.id != myUid }.let { list ->
                    when (orderBy) {
                        DATA.NAME -> list.sortedBy { it.username }
                        else -> list.sortedByDescending { it.timestamp }
                    }
                }

                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        users = filteredUsers,
                        count = filteredUsers.size
                    ) 
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading users")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class UsersUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val count: Int = 0,
    val currentType: String = DATA.TIMESTAMP
)
