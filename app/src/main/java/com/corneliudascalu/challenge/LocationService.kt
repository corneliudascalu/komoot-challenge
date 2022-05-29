package com.corneliudascalu.challenge

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

const val CHANNEL_ID = "ServiceChannel"

@SuppressLint("MissingPermission")
class LocationService : Service() {
    private val locationRequest = LocationRequest.create().apply {
        interval = 3000
        smallestDisplacement = 50f
    }
    private val coroutineScope = CoroutineScope(Dispatchers.IO + Job())
    private val fusedLocationClient: FusedLocationProviderClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val locationsFlow: Flow<Location>
        get() = callbackFlow {
            val callback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    Log.d("LocationService", "Received location ${locationResult.lastLocation}")
                    //Toast.makeText(applicationContext, "New location received ${locationResult.lastLocation}", Toast.LENGTH_SHORT).show()
                    trySend(locationResult.lastLocation)
                }
            }
            fusedLocationClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())
            awaitClose { fusedLocationClient.removeLocationUpdates(callback) }
        }

    private lateinit var _photoFlow: StateFlow<FlickrPhoto?>

    val photoFlow: StateFlow<FlickrPhoto?>
        get() = _photoFlow

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
        startWalking()
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder {
        return LocationBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {
        return super.onUnbind(intent)
    }

    @SuppressLint("MissingPermission")
    fun startWalking() {
        _photoFlow = locationsFlow
            .map { location -> FlickrRepo.getOnePhoto(location.latitude, location.longitude) }
            .onEach { photo -> if (photo != null) FlickrRepo.store(photo) }
            .stateIn(
                coroutineScope,
                SharingStarted.Eagerly,
                initialValue = null
            )
    }

    fun stopWalking() {
        coroutineScope.cancel()
    }


}