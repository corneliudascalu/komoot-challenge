package com.corneliudascalu.challenge

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startForegroundService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.corneliudascalu.challenge.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val adapter = PhotoAdapter()

    private val viewModel by viewModels<WalkViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WalkViewModel(intent?.extras?.getBoolean(EXTRA_USER_WALKING) ?: false) as T
            }
        }
    }
    private val isUserWalking: Boolean get() = viewModel.uiState.value?.isUserWalking ?: false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.uiState.observe(this) { state ->
            invalidateOptionsMenu()
            adapter.submitList(state.photos.map { it.url })
            binding.recyclerView.scrollToPosition(0)

            // TODO Extract location service logic to a delegate or something
            toggleLocationService(isUserWalking)
        }

        preparePermissionLauncher()
    }

    override fun onStart() {
        super.onStart()
        if (isUserWalking) {
            bindService(Intent(this, LocationService::class.java), locationServiceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        locationService?.also { unbindService(locationServiceConnection) }
        locationService = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(if (isUserWalking) R.menu.menu_stop else R.menu.menu_start, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuStart) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                viewModel.start()
            } else {
                requestPermission()
            }
            return true
        }
        if (item.itemId == R.id.menuStop) {
            viewModel.stop()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    private fun preparePermissionLauncher() {
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (it[Manifest.permission.ACCESS_FINE_LOCATION] == true) {
                viewModel.start()
            } else {

                // TODO Only call this once more, and afterwards display a custom explanation
                Toast.makeText(this, "Please grant access to fine location", Toast.LENGTH_SHORT).show()
                permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
            }
        }
    }

    private fun requestPermission() {
        val fineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        if (fineLocationPermission == PackageManager.PERMISSION_DENIED) {
            permissionLauncher.launch(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION))
        } else {
            viewModel.start()
        }
    }

    private val locationServiceConnection = LocationServiceConnection()
    private var locationService: LocationService? = null

    private fun toggleLocationService(isUserWalking: Boolean) {
        if (isUserWalking) {
            if (locationService == null) {
                startForegroundService(applicationContext, Intent(this, LocationService::class.java))
                bindService(Intent(this, LocationService::class.java), locationServiceConnection, BIND_AUTO_CREATE)
            }
        } else {
            locationService?.also {
                unbindService(locationServiceConnection)
                locationService?.stopWalking()
                locationService?.stopForeground(true)
                locationService?.stopSelf()
                locationService = null
            }
        }
    }

    private inner class LocationServiceConnection : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            locationService = (service as LocationService.LocationBinder).getService()
            if (locationService != null) {
                viewModel.walk(locationService!!.photoFlow)
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            locationService = null
        }
    }
}