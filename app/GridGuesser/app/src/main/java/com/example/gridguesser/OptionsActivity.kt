package com.example.gridguesser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.gridguesser.database.GameRepository
import com.example.gridguesser.database.Settings

private const val TAG = "Options"

class OptionsActivity : AppCompatActivity() {
    private lateinit var backBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        var gamerepo = GameRepository.get()
        var settings = gamerepo.currentSettings

        var swchLight = findViewById<Switch>(R.id.switch2)
        var swchTemp = findViewById<Switch>(R.id.switch1)
        var usrname = findViewById<EditText>(R.id.user_name)

        if(settings.use_daylight)
            swchLight.isChecked = true
        if(settings.use_temp)
            swchTemp.isChecked = true

        usrname.setText(settings.username)

        swchLight.setOnCheckedChangeListener() { _, isChecked ->

            if (isChecked) {
                settings.use_daylight = true
                Log.d(TAG, "Toggle daylight sensor on")
            } else {
                settings.use_daylight = false
                Log.d(TAG, "Toggle daylight sensor off")
            }

            gamerepo.updateSettings(settings)
        }

        swchTemp.setOnCheckedChangeListener() { _, isChecked ->

            if (isChecked) {
                settings.use_temp = true
                Log.d(TAG, "Toggle temp sensor on")
            } else {
                settings.use_temp = false
                Log.d(TAG, "Toggle temp sensor off")
            }

            gamerepo.updateSettings(settings)
        }

        usrname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                settings.username = s.toString()
                gamerepo.updateSettings(settings)
                Log.d(TAG, "Username changed")
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        backBtn = findViewById(R.id.backToMain)
        backBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, OptionsActivity::class.java)
        }
    }
}