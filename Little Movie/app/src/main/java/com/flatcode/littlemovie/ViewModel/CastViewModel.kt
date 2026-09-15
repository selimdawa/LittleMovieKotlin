package com.flatcode.littlemovie.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovie.Model.Cast
import com.flatcode.littlemovie.Unit.DATA
import com.flatcode.littlemovie.Repository.CastRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CastViewModel @Inject constructor(
    private val repository: CastRepository
) : ViewModel() {

    private val _castList = MutableStateFlow<List<Cast>>(emptyList())
    val castList: StateFlow<List<Cast>> = _castList

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _castCount = MutableStateFlow(0)
    val castCount: StateFlow<Int> = _castCount

    fun getData(orderBy: String?) {
        if (orderBy == null) return
        _isLoading.value = true
        viewModelScope.launch {
            repository.getCast(orderBy).collectLatest { list ->
                _castList.value = list
                _castCount.value = list.size
                _isLoading.value = false
                Timber.d("Cast updated: %d", list.size)
            }
        }
    }
}
