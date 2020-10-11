package com.example.gridguesser

import android.os.Bundle
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData


class GameActivity : AppCompatActivity() {
    private var state = 0; // 0 - place ships, 1 - player one turn, 2- player two turn
    private var ships = 0;
    private lateinit var gridView: GridView
    private var playerOneBoard = arrayOf(
        " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "1", "", "", "3", "", "", "", "", "", "", "",
        "2", "", "", "3", "", "", "", "", "", "", "",
        "3", "", "", "3", "", "", "", "", "", "", "",
        "4", "", "", "3", "", "", "", "", "", "", "",
        "5", "", "", "3", "", "", "", "", "", "", "",
        "6", "", "", "3", "", "", "", "", "", "", "",
        "7", "", "", "3", "", "", "", "", "", "", "",
        "8", "", "", "3", "", "", "", "", "", "", "",
        "9", "", "", "3", "", "", "", "", "", "", "",
        "10", "", "", "3", "", "", "", "", "", "", ""

    )

    private var playerTwoBoard = arrayOf(
        " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "1", "", "", "2", "", "", "", "", "", "", "",
        "2", "", "", "2", "", "", "", "", "", "", "",
        "3", "", "", "3", "", "", "", "", "", "", "",
        "4", "", "", "3", "", "", "", "", "", "", "",
        "5", "", "", "3", "", "", "", "", "", "", "",
        "6", "", "", "2", "", "", "", "", "", "", "",
        "7", "", "", "", "", "", "", "", "", "", "",
        "8", "", "", "2", "", "", "", "", "", "", "",
        "9", "", "", "3", "", "", "", "", "", "", "",
        "10", "", "", "3", "", "", "", "", "", "", ""

    )
    //fun getState(): LiveData<Int> = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        var userTurn = findViewById(R.id.userTurn) as TextView
        userTurn.text = ships.toString()


        var buttons = arrayOf<Button>()
        playerOneBoard.forEach {
            var newBtn = Button(this)
            newBtn.text = it
            buttons.plus(newBtn)
        }

        gridView = findViewById(R.id.gridview) as GridView
        val adapter = SpaceAdapter(this, playerOneBoard, ships)
        gridView.adapter = adapter
    }
}