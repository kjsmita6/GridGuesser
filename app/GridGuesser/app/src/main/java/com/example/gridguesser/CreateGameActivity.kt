package com.example.gridguesser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.gridguesser.deviceID.DeviceID
import com.example.gridguesser.http.ServerInteractions

private const val TAG = "GridGuesser"
class CreateGameActivity : AppCompatActivity() {
    lateinit var serverInteractions: ServerInteractions
    lateinit var gameCode: LiveData<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        serverInteractions = ServerInteractions.get()
        gameCode = serverInteractions.newGame("some title", DeviceID.getDeviceID(contentResolver))
        gameCode.observe(
            this,
            Observer { responseString ->
                Log.d(TAG, "Response received: $responseString")
            })
    }
}