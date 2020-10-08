package com.example.gridguesser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.lifecycle.Observer
import com.example.gridguesser.activegames.ActiveGamesActivity
import com.example.gridguesser.deviceID.DeviceID
import com.example.gridguesser.http.ServerInteractions

private const val TAG = "GridGuesser"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Log.d(TAG, DeviceID.getDeviceID(contentResolver))

        ServerInteractions.get().serverStatus.observe(
            this,
            Observer { responseString ->
                Log.d(TAG, "Response received: $responseString")
            })

        findViewById<Button>(R.id.active_games)?.setOnClickListener {
            activeGames()
        }

        findViewById<Button>(R.id.create_game)?.setOnClickListener {
            newGame()
        }
    }

    private fun activeGames(){
        val intent = ActiveGamesActivity.newIntent(this@MainActivity)
        startActivity(intent)
    }

    private fun newGame(){
        val intent = CreateGameActivity.newIntent(this@MainActivity)
        startActivity(intent)
    }
}