package com.example.gridguesser

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class GameActivity : AppCompatActivity() {
    private lateinit var state : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        state = findViewById<TextView>(R.id.userTurn).text.toString()
    }
}