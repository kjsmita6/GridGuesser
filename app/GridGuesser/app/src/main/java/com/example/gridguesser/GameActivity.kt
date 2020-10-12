package com.example.gridguesser

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.gridguesser.database.GameRepository

private const val TAG = "GameActivity"

class GameActivity : AppCompatActivity() {
    private lateinit var gridView: GridView
    private lateinit var opp_Btn: Button
    private lateinit var my_Btn: Button
    private lateinit var userTurn: TextView
    private lateinit var boardTitle: TextView

    private var initialShips = 5

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

        var GameRepoo = GameRepository.get()


        opp_Btn = findViewById(R.id.goToOpponent)
        my_Btn = findViewById(R.id.goToPlayer)
        userTurn = findViewById(R.id.userTurn)
        boardTitle = findViewById(R.id.boardTitle)

        updateGameView(GameRepoo.state, GameRepoo.ships.value!!)

            GameRepoo.ships.observe(
            this,
            Observer { ships ->
                ships?.let {
                    Log.d("GameActivity","ships was changed")
                    //userTurn.text = "Place Ships:"+ (initialShips.minus(GameRepoo.ships.value!!)).toString()
                    if(initialShips == GameRepoo.ships.value){
                        GameRepoo.state = 1
                        //send game board to server
                    }
                    updateGameView(GameRepoo.state, GameRepoo.ships.value!!)

                }
            })

        setupBoard(playerOneBoard)

        opp_Btn.setOnClickListener {
            setupBoard((playerTwoBoard))
            my_Btn.visibility= View.VISIBLE
            opp_Btn.visibility= View.INVISIBLE
            boardTitle.text = resources.getString(R.string.opp_ships)
        }

        my_Btn.setOnClickListener {
            setupBoard(playerOneBoard)
            opp_Btn.visibility = View.VISIBLE
            my_Btn.visibility = View.INVISIBLE
            boardTitle.text = resources.getString(R.string.your_ships)
        }

    }

    fun setupBoard (playerBoard: Array<String>) {
        gridView = findViewById(R.id.gridview) as GridView
        val adapter = SpaceAdapter(this, playerBoard)
        gridView.adapter = adapter
    }

    //updates the view based on the state
    fun updateGameView (state: Int, numShips: Int) {
        when(state){
            0 -> { //placing ships
                userTurn.text = "Place Ships:"+ (initialShips -numShips).toString()
                opp_Btn.visibility= View.INVISIBLE
                my_Btn.visibility= View.INVISIBLE
            }
            1 -> {
                userTurn.text = "Player One's Turn"
                opp_Btn.visibility = View.VISIBLE
                my_Btn.visibility = View.INVISIBLE
            }
            2-> {
                userTurn.text = "Player Two's Turn"
//                my_Btn.visibility = View.VISIBLE
//                opp_Btn.visibility = View.INVISIBLE
            }
            else -> {
                Log.d( TAG, "something is wrong")
            }
        }
    }
}