package com.corneliudascalu.challenge

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.corneliudascalu.challenge.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var started = false
    val adapter = PhotoAdapter()
    lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    // TODO
    val viewModel = WalkViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        viewModel.photos.observe(this) { state ->
            started = state.startVisible.not()
            invalidateOptionsMenu()
            adapter.submitList(state.photos.map { it.url })
            binding.recyclerView.scrollToPosition(0)
        }

        preparePermissionLauncher()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        menuInflater.inflate(if (started) R.menu.menu_stop else R.menu.menu_start, menu)
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
}