package com.example.gridguesser

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.GridView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.gridguesser.database.GameRepository


private const val TAG = "GridGuesser"
private const val GAMEID = "game_id"

class GameActivity : AppCompatActivity() {
    private lateinit var gridView: GridView
    private lateinit var opp_Btn: Button
    private lateinit var my_Btn: Button
    private lateinit var userTurn: TextView
    private lateinit var boardTitle: TextView

    private lateinit var help: Button
    private lateinit var home: Button

    private var initialShips = 5
    private var gameID: Int = -1

    private var playerOneBoard = arrayOf(
        " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "1", "", "", "", "", "", "", "", "", "", "",
        "2", "", "", "", "", "", "", "", "", "", "",
        "3", "", "", "", "", "", "", "", "", "", "",
        "4", "", "", "", "", "", "", "", "", "", "",
        "5", "", "", "", "", "", "", "", "", "", "",
        "6", "", "", "", "", "", "", "", "", "", "",
        "7", "", "", "", "", "", "", "", "", "", "",
        "8", "", "", "", "", "", "", "", "", "", "",
        "9", "", "", "", "", "", "", "", "", "", "",
        "10", "", "", "", "", "", "", "", "", "", ""

    )

    private var playerTwoBoard = arrayOf(
        " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "1", "", "", "", "", "", "", "", "", "", "",
        "2", "", "", "", "", "", "", "", "", "", "",
        "3", "", "", "", "", "", "", "", "", "", "",
        "4", "", "", "", "", "", "", "", "", "", "",
        "5", "", "", "", "", "", "", "", "", "", "",
        "6", "", "", "", "", "", "", "", "", "", "",
        "7", "", "", "", "", "", "", "", "", "", "",
        "8", "", "", "", "", "", "", "", "", "", "",
        "9", "", "", "", "", "", "", "", "", "", "",
        "10", "", "", "", "", "", "", "", "", "", ""

    )
    //fun getState(): LiveData<Int> = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        var GameRepo = GameRepository.get()

        val intent = intent
        gameID = intent.getIntExtra(GAMEID, -1)
        if(gameID == -1){
            gameID = GameRepo.id
        } else {
            GameRepo.id = gameID
        }

        //using gameID, ask server for all game info
        //convert game boards to array of states
            //don't show opponents ships (1's -> 0's)
            //determine number of ships remaining for each player
        //Handle different game states


        opp_Btn = findViewById(R.id.goToOpponent)
        my_Btn = findViewById(R.id.goToPlayer)
        userTurn = findViewById(R.id.userTurn)
        boardTitle = findViewById(R.id.boardTitle)
        help = findViewById(R.id.help)
        home = findViewById(R.id.home)

        updateGameView(GameRepo.state, GameRepo.remainingShips.value!!)

            GameRepo.remainingShips.observe(
            this,
            Observer { ships ->
                ships?.let {
                    Log.d(TAG,"ships was changed")
                    //userTurn.text = "Place Ships:"+ (initialShips.minus(GameRepoo.ships.value!!)).toString()
                    if(initialShips == GameRepo.remainingShips.value){
                        GameRepo.state = 1
                        //TODO: send game board to server
                    }
                    updateGameView(GameRepo.state, GameRepo.remainingShips.value!!)

                }
            })

        setupBoard(playerOneBoard)

        opp_Btn.setOnClickListener {
            setupBoard((playerTwoBoard))
            my_Btn.visibility= View.VISIBLE
            opp_Btn.visibility= View.INVISIBLE
            boardTitle.text = resources.getString(R.string.opponents_ships)
        }

        my_Btn.setOnClickListener {
            setupBoard(playerOneBoard)
            opp_Btn.visibility = View.VISIBLE
            my_Btn.visibility = View.INVISIBLE
            boardTitle.text = resources.getString(R.string.your_ships)
        }

        help.setOnClickListener {
            //TODO Go to help screen
            val intent = Intent(this, RulesActivity::class.java)
            startActivity(intent)
        }

        home.setOnClickListener {
            //TODO save game state
            //TODO go to home
            val intent = MainActivity.newIntent(this)
            startActivity(intent)
        }
    }

    private fun setupBoard (playerBoard: Array<String>) {
        gridView = findViewById(R.id.gridview)
        val adapter = SpaceAdapter(this, playerBoard)
        gridView.adapter = adapter
    }

    //updates the view based on the state
    private fun updateGameView (state: Int, numShips: Int) {
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

    companion object{
        fun newIntent(packageContext: Context, id: Int): Intent {
            return Intent(packageContext, GameActivity::class.java).apply {
                putExtra(GAMEID, id)
            }
        }
    }

}