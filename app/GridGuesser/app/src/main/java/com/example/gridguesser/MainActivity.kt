package com.example.gridguesser

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.gridguesser.deviceID.DeviceID
import com.example.gridguesser.http.ServerInteractions


private const val TAG = "GridGuesser"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d("MAIN_APP", DeviceID.getDeviceID(contentResolver))

        ServerInteractions.get().serverStatus.observe(
            this,
            Observer { responseString ->
                Log.d(TAG, "Response received: $responseString")
            })

        val btnCreateGame = findViewById<Button>(R.id.create_game)
        val btnJoinGame = findViewById<Button>(R.id.join_game)
        val btnActiveGames = findViewById<Button>(R.id.active_games)

        btnCreateGame.setOnClickListener {
            startActivity(Intent(this, CreateGameActivity::class.java))
        }

        btnJoinGame.setOnClickListener {
            startActivity(Intent(this, JoinActivity::class.java))
        }

        btnActiveGames.setOnClickListener {
            startActivity(Intent(this, ActiveGamesActivity::class.java))
        }

    }
}