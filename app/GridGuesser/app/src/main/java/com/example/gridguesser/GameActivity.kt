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
import com.example.gridguesser.deviceID.DeviceID
import com.example.gridguesser.http.ServerInteractions


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
    private val gameRepo = GameRepository.get()
    private val serverInteractions = ServerInteractions.get()
    private lateinit var deviceID: String

    private var playerOneBoard = mutableListOf(
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

    private var playerTwoBoard = mutableListOf(
        " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
        "1", "", "", "", "", "", "", "", "", "", "",
        "2", "", "", "", "", "", "", "", "", "", "",
        "3", "", "", "", "", "", "", "", "", "", "",
        "4", "", "", "", "", "", "", "", "", "", "",
        "5", "", "", "", "", "", "", "1", "", "", "",
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
        deviceID = DeviceID.getDeviceID(contentResolver)

        val intent = intent
        gameID = intent.getIntExtra(GAMEID, -1)
        if(gameID == -1){
            gameID = gameRepo.id
        } else {
            gameRepo.id = gameID
        }

        //loadBoards()

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

        updateGameView(gameRepo.state, gameRepo.remainingShips.value!!)

            gameRepo.remainingShips.observe(
            this,
            Observer { ships ->
                ships?.let {
                    Log.d(TAG,"ships was changed")
                    //userTurn.text = "Place Ships:"+ (initialShips.minus(GameRepoo.ships.value!!)).toString()
                    if(initialShips == gameRepo.remainingShips.value){
                        gameRepo.state = 1
                        gameRepo.remainingShips.value = -1
                        placeShips()
                    }
                    updateGameView(gameRepo.state, gameRepo.remainingShips.value!!)

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

    private fun placeShips(){
        var board = "["
        for(i in 1..10){
            board += "["
            for(j in 1..10){
                board +=  "{\"x\":${i-1}, \"y\":${j-1}, \"state\":"
                board += if(playerOneBoard[11 * i + j].isNotEmpty()){
                    playerOneBoard[11*i + j]
                } else {
                    "0"
                }
                board += if(j==10){
                    "}"
                } else {
                    "},"
                }
            }

            board += if(i == 10){
                "]"
            } else {
                "],"
            }
        }
        board += "]"
        Log.d(TAG, board)
        serverInteractions.makeBoard(gameID, deviceID, board).observe(
            this,
            Observer { response ->
                response?.let {
                    Log.d(TAG,"Updated board: $response")
                }
            })
    }

    private fun loadBoards(){
        serverInteractions.getBoards(gameID).observe(
            this,
            Observer {response ->
                response?.let {
                    if(response.get("player1").toString() == deviceID){
                        playerOneBoard = parseBoard(response.get("player1_board").toString())
                        playerTwoBoard = parseBoard(response.get("player2_board").toString())
                    } else {
                        playerOneBoard = parseBoard(response.get("player1_board").toString())
                        playerTwoBoard = parseBoard(response.get("player2_board").toString())
                    }
                    setupBoard(playerOneBoard)
                    var toPrint = ""
                    for(i in 0 until playerOneBoard.size){
                        toPrint += playerOneBoard[i] + ""
                        if(i%11 == 0){
                            toPrint += "\n"
                        }
                    }
                    Log.d(TAG, "TO PRINT: $toPrint")
                }
            }
        )
    }

    private fun parseBoard(board: String): MutableList<String>{
        var toReturn: MutableList<String> = mutableListOf(" ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "1")
        val splitBoard = board.split(":")
        var row = 2
        for(i in 1 until splitBoard.size){
            if(i % 3 == 0){
                toReturn.add(splitBoard[i][0].toString())
            }

            if(i % 30 == 0 && row < 11){
                toReturn.add(row.toString())
                row++
            }
        }
        Log.d(TAG, "TO RETURN: $toReturn")
        return toReturn
    }

    private fun setupBoard (playerBoard: MutableList<String>) {
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