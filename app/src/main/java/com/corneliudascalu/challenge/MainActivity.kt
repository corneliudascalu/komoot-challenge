package com.corneliudascalu.challenge

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.corneliudascalu.challenge.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    var started = false
    val adapter = PhotoAdapter()

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
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(if (started) R.menu.menu_stop else R.menu.menu_start, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuStart) {
            viewModel.start()
            return true
        }
        if (item.itemId == R.id.menuStop) {
            viewModel.stop()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}