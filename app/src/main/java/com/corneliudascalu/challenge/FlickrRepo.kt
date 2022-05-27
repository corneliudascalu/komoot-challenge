package com.corneliudascalu.challenge

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class FlickrRepo {
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .build()
    private val retrofit = Retrofit.Builder()
        .client(okHttpClient)
        .baseUrl("https://www.flickr.com")
        .addConverterFactory(MoshiConverterFactory.create())
        .build()
        .create(FlickrApi::class.java)


    suspend fun getOnePhoto(): FlickrPhoto {
        return retrofit.search("21d94b9df684d19bc7afc78ec43e9503", 46.7566693, 23.6075734).photos.photo.random()
    }
}