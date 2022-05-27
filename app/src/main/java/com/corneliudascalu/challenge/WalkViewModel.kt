package com.corneliudascalu.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import kotlin.time.Duration

class WalkViewModel : ViewModel() {
    private var increment = 1
    private val id get() = increment++
    private var isUserWalking = false

    private val _photos: MutableLiveData<ScreenState> = MutableLiveData(ScreenState(true, listOf()))
    private val tick = flow {
        while (isUserWalking) {
            emit(Unit)
            delay(1000)
        }
    }
    val photos: LiveData<ScreenState> get() = _photos

    fun start() {
        isUserWalking = true
        viewModelScope.launch {
            tick.collect {
                val newPhotos = _photos.value?.photos?.toMutableList() ?: mutableListOf()
                newPhotos.add(FlickrPhoto("https://picsum.photos/200/300?id=$id", LocalDateTime.now()))
                _photos.postValue(_photos.value?.copy(startVisible = isUserWalking.not(), photos = newPhotos.sortedDescending()))
            }
        }
    }

    fun stop() {
        isUserWalking = false
        val newPhotos = _photos.value?.photos?.toMutableList() ?: mutableListOf()
        newPhotos.add(FlickrPhoto("https://picsum.photos/200/300?id=$id", LocalDateTime.now()))
        _photos.postValue(_photos.value?.copy(startVisible = isUserWalking.not(), photos = newPhotos))
    }
}

data class ScreenState(val startVisible: Boolean, val photos: List<FlickrPhoto>)