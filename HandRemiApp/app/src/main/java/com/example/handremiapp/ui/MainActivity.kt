package com.example.handremiapp.ui

import android.content.Intent // Import Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.handremiapp.R // Import R class for resources

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main) // Set the layout defined in activity_main.xml

        // Find buttons by their IDs
        val buttonSinglePlayer: Button = findViewById(R.id.buttonSinglePlayer)
        val buttonMultiplayer: Button = findViewById(R.id.buttonMultiplayer)
        val buttonSettings: Button = findViewById(R.id.buttonSettings)

        // Set click listeners for navigation
        buttonSinglePlayer.setOnClickListener {
            // Navigate to game selection screen
            val intent = Intent(this, GameSelectionActivity::class.java)
            startActivity(intent)
        }

        buttonMultiplayer.setOnClickListener {
            // TODO: Navigate to Bluetooth setup/discovery screen
            showToast("Multiplayer Clicked (Navigation Pending)")
            // Example: val intent = Intent(this, BluetoothSetupActivity::class.java)
            // startActivity(intent)
        }

        buttonSettings.setOnClickListener {
            // TODO: Navigate to settings screen
            showToast("Settings Clicked (Navigation Pending)")
            // Example: val intent = Intent(this, SettingsActivity::class.java)
            // startActivity(intent)
        }
    }

    // Helper function to show Toast messages
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

