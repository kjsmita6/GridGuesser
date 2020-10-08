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
import com.example.gridguesser.deviceID.DeviceID
import com.example.gridguesser.http.ServerInteractions

private const val TAG = "GridGuesser"
class CreateGameActivity : AppCompatActivity() {
    private lateinit var serverInteractions: ServerInteractions
    lateinit var codeLabel: TextView //displays "code:"
    lateinit var codeField: EditText //displays the 4 digit code
    lateinit var createGame: Button //Create game button
    lateinit var waitingText: TextView //waiting for player

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
    }

    private fun submitGame(){
        val gameCode = serverInteractions.newGame(codeField.text.toString(), DeviceID.getDeviceID(contentResolver))
        createGame.isEnabled = false
        gameCode.observe(
            this,
            Observer { responseString ->
                Log.d(TAG, "Response received: $responseString, ${responseString.get("code")}")
                codeLabel.text = getString(R.string.code_label)
                codeField.setText(responseString.get("code").toString().replace("\"", ""))
                codeField.isEnabled = false
                createGame.isEnabled = false
                waitingText.visibility = View.VISIBLE
                //TODO: ADD TO LOCAL DB FOR ACTIVE GAMES
            })

    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, CreateGameActivity::class.java)
        }
    }
}