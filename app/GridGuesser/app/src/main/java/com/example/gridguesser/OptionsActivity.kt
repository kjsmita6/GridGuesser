package com.example.gridguesser

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.example.gridguesser.database.GameRepository
import com.example.gridguesser.database.Settings

private const val TAG = "Options"

class OptionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        var gamerepo = GameRepository.get()
        var settings = gamerepo.currentSettings

        var swchLight = findViewById<Switch>(R.id.switch2)
        var swchTemp = findViewById<Switch>(R.id.switch1)
        var usrname = findViewById<EditText>(R.id.user_name)

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

            if (isChecked)
                settings.use_temp = true
            else
                settings.use_temp = true

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
    }
}