package com.flatcode.littlemovieadmin.ui.profile

import androidx.lifecycle.ViewModel
import com.flatcode.littlemovieadmin.model.Category
import com.flatcode.littlemovieadmin.model.User
import com.flatcode.littlemovieadmin.utils.DATA
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
class ProfileViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    fun init(profileId: String) {
        _uiState.update {
            it.copy(
                profileId = profileId, isMyProfile = profileId == DATA.FirebaseUserUid
            )
        }
        loadData()
    }

    fun loadData() {
        val profileId = _uiState.value.profileId ?: return
        loadUserInfo(profileId)
        loadCounts(profileId)
    }

    private fun loadUserInfo(profileId: String) {
        FirebaseDatabase.getInstance().getReference(DATA.USERS).child(profileId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    _uiState.update { it.copy(user = user) }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadCounts(profileId: String) {
        val database = FirebaseDatabase.getInstance()

        database.getReference(DATA.FAVORITES).child(profileId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    _uiState.update { it.copy(favoritesCount = snapshot.childrenCount.toInt()) }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        if (_uiState.value.isMyProfile) {
            loadItemsCount(DATA.CAST) { count -> _uiState.update { it.copy(castCount = count) } }
            loadItemsCount(DATA.CATEGORIES) { count -> _uiState.update { it.copy(categoriesCount = count) } }
        } else {
            loadInterestedCount(
                profileId, DATA.CAST
            ) { count -> _uiState.update { it.copy(castCount = count) } }
            loadInterestedCount(profileId, DATA.CATEGORIES) { count ->
                _uiState.update {
                    it.copy(
                        categoriesCount = count
                    )
                }
            }
        }
    }

    private fun loadInterestedCount(profileId: String, path: String, onCount: (Int) -> Unit) {
        FirebaseDatabase.getInstance().getReference(DATA.INTERESTED).child(profileId).child(path)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onCount(snapshot.childrenCount.toInt())
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    private fun loadItemsCount(path: String, onCount: (Int) -> Unit) {
        val profileId = _uiState.value.profileId ?: return
        FirebaseDatabase.getInstance().getReference(path)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var i = 0
                    for (child in snapshot.children) {
                        val item = child.getValue(Category::class.java)
                        if (item?.publisher == profileId) i++
                    }
                    onCount(i)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}

data class ProfileUiState(
    val profileId: String? = null,
    val isMyProfile: Boolean = false,
    val user: User? = null,
    val favoritesCount: Int = 0,
    val castCount: Int = 0,
    val categoriesCount: Int = 0
)