package com.flatcode.littlemovieadmin.ui.editorschoice

import androidx.lifecycle.ViewModel
import com.flatcode.littlemovieadmin.model.EditorsChoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class EditorsChoiceViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(EditorsChoiceUiState())
    val uiState: StateFlow<EditorsChoiceUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        val list = mutableListOf<EditorsChoice>()
        for (i in 1..50) {
            list.add(EditorsChoice(i))
        }
        _uiState.update { it.copy(items = list) }
    }
}

data class EditorsChoiceUiState(
    val items: List<EditorsChoice> = emptyList()
)
