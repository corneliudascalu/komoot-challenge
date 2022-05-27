package com.corneliudascalu.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDateTime

class WalkViewModel : ViewModel() {
    private var increment = 1
    private val id get() = increment++

    private val _photos: MutableLiveData<ScreenState> = MutableLiveData(ScreenState(true, listOf()))
    val photos: LiveData<ScreenState> get() = _photos

    fun start() {
        val newPhotos = _photos.value?.photos?.toMutableList() ?: mutableListOf()
        newPhotos.add(FlickrPhoto("https://picsum.photos/200/300?id=$id", LocalDateTime.now()))
        _photos.postValue(_photos.value?.copy(startVisible = false, photos = newPhotos.sortedDescending()))
    }

    fun stop() {
        val newPhotos = _photos.value?.photos?.toMutableList() ?: mutableListOf()
        newPhotos.add(FlickrPhoto("https://picsum.photos/200/300?id=$id", LocalDateTime.now()))
        _photos.postValue(_photos.value?.copy(startVisible = true, photos = newPhotos))
    }
}

data class ScreenState(val startVisible: Boolean, val photos: List<FlickrPhoto>)