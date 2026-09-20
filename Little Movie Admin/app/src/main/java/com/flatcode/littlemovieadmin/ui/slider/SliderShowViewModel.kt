package com.flatcode.littlemovieadmin.ui.slider

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.repository.SliderRepository
import com.flatcode.littlemovieadmin.utils.CloudinaryHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SliderShowViewModel @Inject constructor(
    private val repository: SliderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SliderShowUiState())
    val uiState: StateFlow<SliderShowUiState> = _uiState.asStateFlow()

    init {
        loadSliderShow()
    }

    fun loadSliderShow() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                val images = repository.getSliderImages()
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        images = images,
                        itemCount = images.filter { it.value.isNotEmpty() }.size
                    ) 
                }
            } catch (e: Exception) {
                Timber.e(e, "Error loading slider images")
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun uploadImage(imageUri: Uri, name: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val url = CloudinaryHelper.uploadFile(imageUri)
                repository.updateSliderImage(name, url)
                loadSliderShow()
                onResult(true, "The photo has been posted")
            } catch (e: Exception) {
                onResult(false, e.message)
            }
        }
    }
}

data class SliderShowUiState(
    val isLoading: Boolean = false,
    val images: Map<String, String> = emptyMap(),
    val itemCount: Int = 0
)
