package com.example.gridguesser

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.gridguesser.activegames.ActiveGamesActivity
import com.example.gridguesser.database.GameRepository
import com.example.gridguesser.deviceID.DeviceID
import com.example.gridguesser.http.ServerInteractions

private const val TAG = "GridGuesser"

class MainActivity : AppCompatActivity() {
    private lateinit var settingsRepo: GameRepository
    private lateinit var serverInteractions: ServerInteractions
    private lateinit var deviceID: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        deviceID = DeviceID.getDeviceID(contentResolver)

        Log.d(TAG, deviceID)
        serverInteractions = ServerInteractions.get()
        serverInteractions.serverStatus.observe(
            this,
            Observer { responseString ->
                Log.d(TAG, "Response received: $responseString")
            })

        settingsRepo = GameRepository.get()
        settingsRepo.getSettings().observe(
            this,
            Observer { responseList ->
                if(responseList.isEmpty()){
                    settingsRepo.addSettings(settingsRepo.currentSettings)
                    findViewById<TextView>(R.id.user_welcome).text = "Welcome, ${settingsRepo.currentSettings.username}"
                    serverInteractions.addUser(deviceID)

                } else {
                    settingsRepo.currentSettings = responseList[0]
                    findViewById<TextView>(R.id.user_welcome).text = "Welcome, ${responseList[0].username}"
                }
                Log.d(TAG, "FOUND SETTINGS: $responseList")
            }
        )

        findViewById<Button>(R.id.active_games)?.setOnClickListener {
            activeGames()
        }

        findViewById<Button>(R.id.create_game)?.setOnClickListener {
            newGame()
        }

        findViewById<Button>(R.id.join_game)?.setOnClickListener {
            joinGame()
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

    private fun joinGame(){
        val intent = JoinActivity.newIntent(this@MainActivity)
        startActivity(intent)
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, MainActivity::class.java)
        }
    }
}