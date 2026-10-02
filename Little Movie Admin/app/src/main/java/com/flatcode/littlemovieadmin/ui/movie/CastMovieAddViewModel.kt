package com.flatcode.littlemovieadmin.ui.movie

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.model.Cast
import com.flatcode.littlemovieadmin.repository.CastRepository
import com.flatcode.littlemovieadmin.utils.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CastMovieAddViewModel @Inject constructor(
    private val repository: CastRepository
) : ViewModel() {

    private val _castList = MutableStateFlow<List<Cast>>(emptyList())
    val castList: StateFlow<List<Cast>> = _castList.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCast()
    }

    fun loadCast() {
        viewModelScope.launch {
            repository.getCastList(DATA.TIMESTAMP).collectLatest { list ->
                _castList.value = list
                _isLoading.value = false
            }
        }
    }
}
