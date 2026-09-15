package com.flatcode.littlemovieadmin.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.Model.Cast
import com.flatcode.littlemovieadmin.Repository.CastRepository
import com.flatcode.littlemovieadmin.Unit.DATA
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CastViewModel @Inject constructor(
    private val repository: CastRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CastUiState())
    val uiState: StateFlow<CastUiState> = _uiState.asStateFlow()

    fun getData(orderBy: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, currentType = orderBy) }
            try {
                val castList = repository.getCastList(orderBy)
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        castList = castList,
                        count = castList.size
                    ) 
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading cast")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
}

data class CastUiState(
    val isLoading: Boolean = false,
    val castList: List<Cast> = emptyList(),
    val count: Int = 0,
    val currentType: String = DATA.TIMESTAMP
)
