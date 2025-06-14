package com.example.handremiapp.data.bluetooth

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothServerSocket
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.IOException
import java.util.UUID

// SuppressMissingPermission is needed because permission checks happen elsewhere (e.g., in ViewModel/Activity)
@SuppressLint("MissingPermission")
class BluetoothController(private val context: Context) {

    // Unique UUID for this application's Bluetooth service
    private val SERVICE_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // Standard SPP UUID
    private val SERVICE_NAME = "HandRemiAppBluetoothService"

    private val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
    private val bluetoothAdapter: BluetoothAdapter? = bluetoothManager?.adapter

    private val _scanResults = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    val scanResults: StateFlow<List<BluetoothDevice>> = _scanResults.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private val _receivedMessages = MutableStateFlow<BluetoothMessage?>(null)
    val receivedMessages: StateFlow<BluetoothMessage?> = _receivedMessages.asStateFlow()

    private var serverSocket: BluetoothServerSocket? = null
    private var clientSocket: BluetoothSocket? = null
    private var communicationJob: Job? = null

    // TODO: Implement BroadcastReceivers for discovery and state changes
    // private val discoveryReceiver = ...
    // private val stateReceiver = ...

    init {
        // TODO: Register BroadcastReceivers
        // context.registerReceiver(discoveryReceiver, IntentFilter(BluetoothDevice.ACTION_FOUND))
        // context.registerReceiver(stateReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    fun startDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) return
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
        _scanResults.value = emptyList() // Clear previous results
        bluetoothAdapter?.startDiscovery()
        println("Bluetooth discovery started...")
    }

    fun stopDiscovery() {
        if (!hasPermission(Manifest.permission.BLUETOOTH_SCAN)) return
        if (bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
            println("Bluetooth discovery stopped.")
        }
    }

    fun startServer(scope: CoroutineScope) {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) return
        _connectionState.value = ConnectionState.Listening
        scope.launch(Dispatchers.IO) {
            try {
                serverSocket = bluetoothAdapter?.listenUsingRfcommWithServiceRecord(SERVICE_NAME, SERVICE_UUID)
                println("Bluetooth server started, waiting for connection...")
                val socket = serverSocket?.accept() // Blocking call
                println("Bluetooth connection accepted.")
                serverSocket?.close() // Close listening socket after connection
                handleConnection(socket, scope)
            } catch (e: IOException) {
                println("Bluetooth server error: ${e.message}")
                _connectionState.postValue(ConnectionState.Error("Server failed: ${e.message}"))
                closeConnection()
            }
        }
    }

    fun connectToServer(device: BluetoothDevice, scope: CoroutineScope) {
        if (!hasPermission(Manifest.permission.BLUETOOTH_CONNECT)) return
        _connectionState.value = ConnectionState.Connecting
        scope.launch(Dispatchers.IO) {
            try {
                val socket = device.createRfcommSocketToServiceRecord(SERVICE_UUID)
                stopDiscovery() // Discovery interferes with connection
                socket.connect() // Blocking call
                println("Connected to Bluetooth server.")
                handleConnection(socket, scope)
            } catch (e: IOException) {
                println("Bluetooth client connection error: ${e.message}")
                _connectionState.postValue(ConnectionState.Error("Connection failed: ${e.message}"))
                closeConnection()
            }
        }
    }

    private suspend fun handleConnection(socket: BluetoothSocket?, scope: CoroutineScope) {
        clientSocket = socket
        _connectionState.postValue(ConnectionState.Connected)
        communicationJob = scope.launch(Dispatchers.IO) {
            if (socket == null) {
                _connectionState.postValue(ConnectionState.Error("Socket is null"))
                return@launch
            }
            val inputStream = socket.inputStream
            val outputStream = socket.outputStream
            val buffer = ByteArray(1024)
            var bytes: Int

            try {
                while (true) { // Keep listening for messages
                    bytes = inputStream.read(buffer)
                    val receivedString = String(buffer, 0, bytes)
                    println("Received raw: $receivedString")
                    try {
                        val message = Json.decodeFromString<BluetoothMessage>(receivedString)
                        _receivedMessages.postValue(message)
                    } catch (e: Exception) {
                        println("Serialization error: ${e.message}")
                        // Handle potential partial messages or errors
                    }
                    delay(100) // Small delay to prevent busy-waiting
                }
            } catch (e: IOException) {
                println("Bluetooth communication error: ${e.message}")
                _connectionState.postValue(ConnectionState.Disconnected)
                closeConnection()
            }
        }
    }

    fun sendMessage(message: BluetoothMessage) {
        if (_connectionState.value != ConnectionState.Connected || clientSocket == null) {
            println("Cannot send message: Not connected.")
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonString = Json.encodeToString(message)
                clientSocket?.outputStream?.write(jsonString.toByteArray())
                println("Sent: $jsonString")
            } catch (e: IOException) {
                println("Bluetooth send error: ${e.message}")
                _connectionState.postValue(ConnectionState.Error("Send failed: ${e.message}"))
                closeConnection()
            }
        }
    }

    fun closeConnection() {
        try {
            communicationJob?.cancel()
            serverSocket?.close()
            clientSocket?.close()
            _connectionState.value = ConnectionState.Disconnected
            println("Bluetooth connection closed.")
        } catch (e: IOException) {
            println("Error closing Bluetooth connection: ${e.message}")
        }
    }

    fun cleanup() {
        // TODO: Unregister BroadcastReceivers
        // context.unregisterReceiver(discoveryReceiver)
        // context.unregisterReceiver(stateReceiver)
        closeConnection()
    }

    private fun hasPermission(permission: String): Boolean {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }
}

// Extension function for StateFlow<T?>.postValue equivalent
fun <T> MutableStateFlow<T?>.postValue(newValue: T?) {
    this.value = newValue
}

// Extension function for StateFlow<T>.postValue equivalent
fun <T> MutableStateFlow<T>.postValue(newValue: T) {
    this.value = newValue
}

sealed class ConnectionState {
    object Disconnected : ConnectionState()
    object Listening : ConnectionState()
    object Connecting : ConnectionState()
    object Connected : ConnectionState()
    data class Error(val message: String) : ConnectionState()
}

