package com.example.gridguesser

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.LiveData
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

        findViewById<Button>(R.id.create_game)?.setOnClickListener {
            newGame()
        }
    }

    private fun newGame(){
        val intent = CreateGameActivity.newIntent(this@MainActivity)
        startActivity(intent)
    }
}