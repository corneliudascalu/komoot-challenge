package com.corneliudascalu.challenge

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.time.LocalDateTime

object FlickrRepo {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC })
        .build()
    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://www.flickr.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(FlickrApi::class.java)

    private val photoCache = mutableListOf<WalkPhoto>()


    // 46.7566693, 23.6075734
    suspend fun getOnePhoto(latitude: Double, longitude: Double): FlickrPhoto? {
        // TODO Error handling
        return try {
            // TODO If no photo found, widen the search area by passing a larger radius?
            // TODO Really need to hide my API key better
            retrofit.search("21d94b9df684d19bc7afc78ec43e9503", latitude, longitude).photos.photo.random()
        } catch (e: Exception) {
            null
        }
    }

    fun store(photo: FlickrPhoto) {
        photoCache.add(WalkPhoto(photo.url, LocalDateTime.now()))
    }

    fun getAll(): List<WalkPhoto> {
        return photoCache.sortedDescending()
    }
}