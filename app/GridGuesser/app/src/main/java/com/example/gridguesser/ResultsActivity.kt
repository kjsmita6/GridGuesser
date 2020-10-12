package com.example.gridguesser

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ResultsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val btnMainMenu = findViewById<Button>(R.id.button)
        btnMainMenu.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }
}