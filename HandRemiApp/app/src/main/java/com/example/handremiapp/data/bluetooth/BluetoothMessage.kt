package com.example.handremiapp.data.bluetooth

import kotlinx.serialization.Serializable
import com.example.handremiapp.data.model.Card // Assuming Card is serializable or has a serializable representation
import com.example.handremiapp.data.model.PlayerAction // Assuming PlayerAction is serializable or has a serializable representation
import com.example.handremiapp.data.model.GameState // Assuming GameState is serializable or has a serializable representation

/**
 * Defines the structure of messages exchanged over Bluetooth.
 * Uses kotlinx.serialization for converting objects to/from JSON strings.
 */
@Serializable
sealed class BluetoothMessage {

    @Serializable
    data class PlayerInfo(val playerId: String, val playerName: String) : BluetoothMessage()

    @Serializable
    data class GameSetup(val gameType: String, val playerOrder: List<String>) : BluetoothMessage()

    @Serializable
    data class GameStateUpdate(val gameState: GameState) : BluetoothMessage() // Send full or partial state

    @Serializable
    data class PlayerActionEvent(val playerId: String, val action: PlayerAction) : BluetoothMessage()

    @Serializable
    data class ChatMessage(val senderId: String, val message: String) : BluetoothMessage()

    @Serializable
    data class ErrorMessage(val error: String) : BluetoothMessage()

    @Serializable
    object ConnectionRequest : BluetoothMessage() // Simple request

    @Serializable
    object ConnectionAccepted : BluetoothMessage() // Simple acceptance

    @Serializable
    object Disconnect : BluetoothMessage() // Signal disconnection
}

