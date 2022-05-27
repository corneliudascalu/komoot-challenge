package com.corneliudascalu.challenge

import android.app.Notification
import android.app.NotificationChannel
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

const val CHANNEL_ID = "ServiceChannel"

class LocationService : Service() {
    inner class LocationBinder : Binder() {
        fun getService(): LocationService {
            return this@LocationService
        }
    }

    override fun onCreate() {
        super.onCreate()
        val notificationChannel = NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
            .setName("Default Channel")
            .setDescription("This is the default notification channel")
            .build()
        NotificationManagerCompat.from(this).createNotificationChannel(notificationChannel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Walking")
            .setContentText("Location updates are collected every 100m")
            .setSmallIcon(com.google.android.material.R.drawable.abc_btn_check_material)
            .setContentIntent(PendingIntent.getActivity(this, 0, Intent(this, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE))
            .setTicker("Location updates are collected every 100m")
            .build()

        // Notification ID cannot be 0.
        startForeground(42, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return LocationBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }
}