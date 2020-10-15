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
class CreateGameActivity : AppCompatActivity() {
    private lateinit var serverInteractions: ServerInteractions
    private var gameRepo: GameRepository = GameRepository.get()
    private lateinit var codeLabel: TextView //displays "code:"
    private lateinit var codeField: EditText //displays the 4 digit code
    private lateinit var createGame: Button //Create game button
    private lateinit var waitingText: TextView //waiting for player
    var gameID = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_game)
        serverInteractions = ServerInteractions.get()

        codeLabel = findViewById(R.id.code_label)
        codeField = findViewById(R.id.code_field)
        createGame = findViewById(R.id.create_game)
        waitingText = findViewById(R.id.waiting_text)
        createGame.isEnabled = false

        codeLabel.text = getString(R.string.create_game_title)

        codeField.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null) {
                    createGame.isEnabled = s.isNotEmpty()
                }
            }
        })

        createGame.setOnClickListener {
            submitGame()
        }
        gameRepo.changeFlag.observe(
            this,
            Observer {
                if(gameRepo.eventID == gameID && gameRepo.event == "join"){
                    userJoined()
                }
            }
        )
    }

    private fun submitGame(){
        val gameCode = serverInteractions.newGame(codeField.text.toString(), DeviceID.getDeviceID(contentResolver))
        createGame.isEnabled = false
        codeField.isEnabled = false
        gameCode.observe(
            this,
            Observer { responseString ->
                Log.d(TAG, "Response received: $responseString")
                if(responseString?.get("error") != null) {
                    codeLabel.text = getString(R.string.code_label)
                    createGame.isEnabled = false
                    waitingText.visibility = View.VISIBLE
                    waitingText.text = getString(R.string.waiting_text)
                    val newGame = Game(responseString.get("id").asInt, codeField.text.toString(), gameRepo.currentSettings.username, "Waiting for player...", 0, 0, -1, 1)
                    codeField.setText(responseString.get("code").toString().replace("\"", ""))
                    codeField.isEnabled = false

                    gameRepo.addGame(newGame)
                    gameID = responseString.get("id").asInt
                } else {
                    codeField.isEnabled = true
                    createGame.isEnabled = true
                    waitingText.visibility = View.VISIBLE
                    waitingText.text = getString(R.string.create_error_text)
                }
            })
    }

    //to be called by firebase after a user joins
    private fun userJoined(){
        val intent = GameActivity.newIntent(this@CreateGameActivity, gameID)
        startActivity(intent)
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, CreateGameActivity::class.java)
        }
    }
}