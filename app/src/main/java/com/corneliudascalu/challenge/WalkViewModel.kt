package com.corneliudascalu.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.util.CollectionUtils.listOf
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class WalkViewModel : ViewModel() {
    private var isUserWalking = false

    private val _uiState: MutableLiveData<ScreenState> = MutableLiveData(
        ScreenState(
            isUserWalking = false,
            photos = listOf()
        )
    )
    val uiState: LiveData<ScreenState> get() = _uiState

    fun start() {
        isUserWalking = true
        _uiState.postValue(
            _uiState.value?.copy(
                isUserWalking = isUserWalking,
            )
        )
    }

    fun walk(photoFlow: StateFlow<FlickrPhoto?>) {
        if (isUserWalking) {
            viewModelScope.launch {
                photoFlow
                    .map { FlickrRepo.getAll() }
                    .collect {
                        _uiState.postValue(
                            ScreenState(isUserWalking, it)
                        )
                    }
            }
        }
    }

    fun stop() {
        isUserWalking = false
        _uiState.postValue(
            _uiState.value?.copy(
                isUserWalking = isUserWalking
            )
        )
    }
}

data class ScreenState(val isUserWalking: Boolean, val photos: List<WalkPhoto>)