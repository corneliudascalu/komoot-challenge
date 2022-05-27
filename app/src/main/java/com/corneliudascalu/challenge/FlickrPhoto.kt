package com.corneliudascalu.challenge

import java.time.LocalDateTime

data class FlickrPhoto(val url: String, val date: LocalDateTime) : Comparable<FlickrPhoto> {
    override fun compareTo(other: FlickrPhoto): Int {
        return this.date.compareTo(other.date)
    }
}