package com.flatcode.littlemovieadmin.ui.users

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.User
import com.flatcode.littlemovieadmin.repository.AuthRepository
import com.flatcode.littlemovieadmin.repository.UserRepository
import com.flatcode.littlemovieadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersViewModel @Inject constructor(
    private val repository: UserRepository,
    private val authRepo: AuthRepository
) : ViewModel() {

    private val _users = MutableStateFlow<List<User>>(emptyList())
    private val _searchQuery = MutableStateFlow("")

    val users: StateFlow<List<User>> = combine(_users, _searchQuery) { list, query ->
        if (query.isEmpty()) {
            list
        } else {
            list.filter {
                it.username?.contains(query, ignoreCase = true) == true
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _orderBy = MutableStateFlow(DATA.TIMESTAMP)
    val orderBy: StateFlow<String> = _orderBy.asStateFlow()

    init {
        fetchData()
    }

    private fun fetchData() {
        val myUid = authRepo.getCurrentUserUid()
        viewModelScope.launch {
            _orderBy.collectLatest { order ->
                _isLoading.value = true
                repository.getAllUsers().collectLatest { rawList ->
                    val filtered = rawList.filter { it.id != myUid }.let { list ->
                        when (order) {
                            DATA.NAME -> list.sortedBy { it.username }
                            else -> list.sortedByDescending { it.timestamp }
                        }
                    }
                    _users.value = filtered
                    _isLoading.value = false
                }
            }
        }
    }

    fun getData(orderBy: String) {
        _orderBy.value = orderBy
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}
