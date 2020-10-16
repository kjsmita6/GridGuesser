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
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.gridguesser.database.GameRepository
import com.example.gridguesser.database.Settings
import com.example.gridguesser.deviceID.DeviceID

private const val TAG = "GridGuesser"

class OptionsActivity : AppCompatActivity() {
    private lateinit var backBtn: Button
    private val gamerepo = GameRepository.get()
    private val settings = gamerepo.currentSettings

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_options)

        var swchLight = findViewById<Switch>(R.id.switch2)
        var usrname = findViewById<EditText>(R.id.user_name)
        usrname.setText(settings.username, TextView.BufferType.EDITABLE);
        swchLight.isChecked = settings.use_daylight

        swchLight.setOnCheckedChangeListener() { _, isChecked ->
            settings.use_daylight = isChecked
        }

        usrname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                settings.username = s.toString()
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

    override fun onStop() {
        super.onStop()
        if(settings.username.replace(" ", "").isEmpty()){
            settings.username = DeviceID.getDeviceID(this.contentResolver).substring(0, 5)
        }
        gamerepo.updateSettings(settings)
    }

    companion object {
        fun newIntent(packageContext: Context): Intent {
            return Intent(packageContext, OptionsActivity::class.java)
        }
    }
}