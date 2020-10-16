package com.example.gridguesser

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlin.properties.Delegates

class RulesActivity : AppCompatActivity() {
    private lateinit var backBtn: Button
    private var isMainScreen = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)

        isMainScreen = intent.getBooleanExtra("Heck", true)

        backBtn = findViewById(R.id.backToGame)
        backBtn.setOnClickListener {
            if(isMainScreen){
                val intent = MainActivity.newIntent(this)
                startActivity(intent)
            } else {
                val intent = Intent(this, GameActivity::class.java)
                startActivity(intent)
            }
        }
    }

    fun newIntent(packageContext: Context, mainScreen: Boolean): Intent {
        return Intent(packageContext, RulesActivity::class.java).apply {
            putExtra("Heck", mainScreen)
        }
    }
}