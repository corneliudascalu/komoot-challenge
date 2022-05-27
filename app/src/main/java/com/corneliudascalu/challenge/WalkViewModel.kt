package com.corneliudascalu.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class WalkViewModel : ViewModel() {
    private var isUserWalking = false
    private val _uiState: MutableLiveData<ScreenState> = MutableLiveData(
        ScreenState(
            isUserWalking = false,
            photos = listOf()
        )
    )
    private val flickrRepo = FlickrRepo()
    private val tick = flow {
        while (isUserWalking) {
            emit(Unit)
            delay(3000)
        }
    }
    val uiState: LiveData<ScreenState> get() = _uiState

    fun start() {
        isUserWalking = true
        viewModelScope.launch {
            tick.collect {
                val photo = flickrRepo.getOnePhoto()
                if (photo != null) {
                    val newPhotos = _uiState.value?.photos?.toMutableList() ?: mutableListOf()
                    newPhotos.add(WalkPhoto(photo.url, LocalDateTime.now()))
                    _uiState.postValue(
                        _uiState.value?.copy(
                            isUserWalking = isUserWalking,
                            photos = newPhotos.sortedDescending()
                        )
                    )
                } else {
                    // TODO Widen the search area?
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