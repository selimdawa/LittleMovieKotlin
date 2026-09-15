package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Model.Cast
import com.flatcode.littlemovie.Repository.CastRepository
import com.flatcode.littlemovie.Unit.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MyCastViewModel @Inject constructor(
    private val repository: CastRepository
) : ViewModel() {

    private val _cast = MutableStateFlow<List<Cast>>(emptyList())
    val cast: StateFlow<List<Cast>> = _cast

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun loadCast(orderBy: String) {
        val userId = DATA.FirebaseUserUid ?: return
        _isLoading.value = true
        viewModelScope.launch {
            repository.getInterestedCast(userId, orderBy).collectLatest { list ->
                _cast.value = list
                _isLoading.value = false
                Timber.d("My cast updated: %d", list.size)
            }
        }
    }
}
