package com.corneliudascalu.challenge

import java.time.LocalDateTime

data class WalkPhoto(val url: String, val timestamp: LocalDateTime) : Comparable<WalkPhoto> {
    override fun compareTo(other: WalkPhoto): Int {
        return this.timestamp.compareTo(other.timestamp)
    }
}