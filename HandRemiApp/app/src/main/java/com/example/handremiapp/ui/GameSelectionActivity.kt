package com.example.handremiapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import com.example.handremiapp.R

class GameSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_selection)

        // Find buttons
        val buttonHand: Button = findViewById(R.id.buttonGameHand)
        val buttonHandSaudi: Button = findViewById(R.id.buttonGameHandSaudi)
        val buttonHand500: Button = findViewById(R.id.buttonGameHand500)
        val buttonTarneeb: Button = findViewById(R.id.buttonGameTarneeb)
        val buttonTrix: Button = findViewById(R.id.buttonGameTrix)

        // Set click listeners (placeholder actions)
        buttonHand.setOnClickListener { startGame("Hand (Normal)") }
        buttonHandSaudi.setOnClickListener { startGame("Hand (Saudi)") }
        buttonHand500.setOnClickListener { startGame("Hand 500") }
        buttonTarneeb.setOnClickListener { startGame("Tarneeb") }
        buttonTrix.setOnClickListener { startGame("Trix") }
    }

    private fun startGame(gameType: String) {
        // TODO: Navigate to the actual game screen or setup screen for the selected game
        // For now, just show a Toast
        showToast("Starting $gameType (Navigation Pending)")
        // Example: val intent = Intent(this, GameActivity::class.java)
        // intent.putExtra("GAME_TYPE", gameType)
        // startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

