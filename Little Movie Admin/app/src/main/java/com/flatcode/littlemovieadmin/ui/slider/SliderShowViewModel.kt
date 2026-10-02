package com.flatcode.littlemovieadmin.ui.slider

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flatcode.littlemovieadmin.repository.SliderRepository
import com.flatcode.littlemovieadmin.utils.CloudinaryHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SliderShowViewModel @Inject constructor(
    private val repository: SliderRepository
) : ViewModel() {

    private val _images = MutableStateFlow<Map<String, String>>(emptyMap())
    val images: StateFlow<Map<String, String>> = _images.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val itemCount: StateFlow<Int> = _images.map { map ->
        map.filter { it.value.isNotEmpty() }.size
    }.stateIn(viewModelScope, SharingStarted.Lazily, 0)

    init {
        loadSliderShow()
    }

    fun loadSliderShow() {
        viewModelScope.launch {
            repository.getSliderImages().collectLatest { map ->
                _images.value = map
                _isLoading.value = false
            }
        }
    }

    fun uploadImage(imageUri: Uri, name: String, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                val url = CloudinaryHelper.uploadFile(imageUri)
                repository.updateSliderImage(name, url)
                onResult(true, "The photo has been posted")
            } catch (e: Exception) {
                Timber.e(e, "Error uploading slider image")
                onResult(false, e.message)
            }
        }
    }
}
