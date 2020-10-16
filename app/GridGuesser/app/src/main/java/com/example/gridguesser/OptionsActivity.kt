package com.example.gridguesser

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gridguesser.database.GameRepository
import com.example.gridguesser.database.Settings
import com.example.gridguesser.deviceID.DeviceID

private const val TAG = "GridGuesser"

class OptionsActivity : AppCompatActivity() {
    private val gamerepo = GameRepository.get()
    private val settings = gamerepo.currentSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        var swchLight = findViewById<Switch>(R.id.switch2)
        var swchTemp = findViewById<Switch>(R.id.switch1)
        var usrname = findViewById<EditText>(R.id.user_name)
        usrname.setText(settings.username, TextView.BufferType.EDITABLE);
        swchLight.isChecked = settings.use_daylight
        swchTemp.isChecked = settings.use_temp

        swchLight.setOnCheckedChangeListener() { _, isChecked ->
            settings.use_daylight = isChecked
        }

        swchTemp.setOnCheckedChangeListener() { _, isChecked ->
            settings.use_temp = isChecked
        }

        usrname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                settings.username = s.toString()
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        findViewById<Button>(R.id.go_home).setOnClickListener {
            val intent = MainActivity.newIntent(this)
            startActivity(intent)
        }
    }

    override fun onStop() {
        super.onStop()
        if(settings.username.replace(" ", "").isEmpty()){
            settings.username = DeviceID.getDeviceID(this.contentResolver).substring(0, 5)
        }
        gamerepo.updateSettings(settings)
    }
}