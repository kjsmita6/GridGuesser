package com.example.gridguesser

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import com.example.gridguesser.database.Game
import com.example.gridguesser.database.GameRepository
import com.example.gridguesser.deviceID.DeviceID
import com.example.gridguesser.http.ServerInteractions

private const val TAG = "GridGuesser"
class JoinActivity : AppCompatActivity() {
    private lateinit var serverInteractions: ServerInteractions
    lateinit var codeEntry: EditText
    lateinit var joinButton: Button
    lateinit var errorLabel: TextView
    var gameRepo = GameRepository.get()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join)
        serverInteractions = ServerInteractions.get()

        codeEntry = findViewById(R.id.code_entry)
        joinButton = findViewById(R.id.join_game_button)
        errorLabel = findViewById(R.id.join_error_label)

        codeEntry.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    joinButton.isEnabled = s.length == 4
                }
                errorLabel.visibility = View.INVISIBLE
            }
        })

        joinButton.setOnClickListener {
            joinGame()
        }
    }

    private fun joinGame(){
        val gameCode = serverInteractions.joinGame(codeEntry.text.toString(), DeviceID.getDeviceID(contentResolver))
        joinButton.isEnabled = false
        codeEntry.isEnabled = false
        gameCode.observe(
            this,
            Observer { responseString ->
                Log.d(TAG, "Response received: $responseString")
                if(responseString?.get("error") != null){
                    var newGame = Game(responseString.get("id").asInt, responseString.get("title").toString(),
                        responseString.get("player1").toString(), "username", 0, 0)
                    gameRepo.addGame(newGame)
                    //TODO: ADD TO DB, SWITCH TO GAME SCREEN
                } else {
                    errorLabel.visibility = View.VISIBLE
                    joinButton.isEnabled = true
                    codeEntry.isEnabled = true
                }
            })
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, JoinActivity::class.java)
        }
    }
}