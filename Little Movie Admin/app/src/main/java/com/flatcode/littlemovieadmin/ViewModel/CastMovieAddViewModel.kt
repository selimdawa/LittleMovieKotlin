package com.flatcode.littlemovieadmin.ViewModel

import androidx.lifecycle.ViewModel
import com.flatcode.littlemovieadmin.Model.Cast
import com.flatcode.littlemovieadmin.Unit.DATA
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class CastMovieAddViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(CastMovieAddUiState())
    val uiState: StateFlow<CastMovieAddUiState> = _uiState.asStateFlow()

    init {
        loadCast()
    }

    fun loadCast() {
        _uiState.update { it.copy(isLoading = true) }
        val ref = FirebaseDatabase.getInstance().getReference(DATA.CAST)
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Cast>()
                for (child in snapshot.children) {
                    child.getValue(Cast::class.java)?.let { list.add(it) }
                }
                list.reverse()
                _uiState.update { it.copy(isLoading = false, castList = list) }
            }
            override fun onCancelled(error: DatabaseError) {
                _uiState.update { it.copy(isLoading = false) }
            }
        })
    }
}

data class CastMovieAddUiState(
    val castList: List<Cast> = emptyList(),
    val isLoading: Boolean = false
)
