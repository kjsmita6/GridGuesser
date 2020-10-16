package com.example.gridguesser

import android.content.Context

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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
import com.example.gridguesser.database.Settings
import com.example.gridguesser.deviceID.DeviceID
import com.example.gridguesser.http.ServerInteractions


private const val TAG = "GridGuesser"
private const val GAMEID = "game_id"

class GameActivity : AppCompatActivity(), SensorEventListener, SpaceAdapter.Callbacks {
    private lateinit var gridView: GridView
    private lateinit var opp_Btn: Button
    private lateinit var my_Btn: Button
    private lateinit var userTurn: TextView
    private lateinit var boardTitle: TextView
    private lateinit var sensorManager: SensorManager
    private var pressure: Sensor? = null
    private lateinit var bg: View
    private lateinit var settings: Settings

    private lateinit var help: Button
    private lateinit var home: Button

    private var initialShips = 5
    private var player = -1
    private var gameID: Int = -1
    private val gameRepo = GameRepository.get()
    private val serverInteractions = ServerInteractions.get()
    private lateinit var deviceID: String
    //private var isPlayerOne = true

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
        "5", "", "", "", "", "", "", "", "", "", "",
        "6", "", "", "", "", "", "", "", "", "", "",
        "7", "", "", "", "", "", "", "", "", "", "",
        "8", "", "", "", "", "", "", "", "", "", "",
        "9", "", "", "", "", "", "", "", "", "", "",
        "10", "", "", "", "", "", "", "", "", "", ""

    )
    //fun getState(): LiveData<Int> = 0

    override fun onSquareSelected(position: Int) {
        move(position)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        Log.d(TAG, "CREATING GAME ACTIVITY")

        settings = gameRepo.currentSettings
        deviceID = DeviceID.getDeviceID(contentResolver)

        val intent = intent
        gameID = intent.getIntExtra(GAMEID, -1)
        if(gameID == -1){
            gameID = gameRepo.id
        } else {
            gameRepo.id = gameID
        }

        getWhichPlayer()

        gameRepo.getGame(gameID.toString()).observe(
            this,
            Observer { thisGame ->
                    gameRepo.state = thisGame.status
                    Log.d(TAG, "INITIAL STATUS: ${gameRepo.state}")
                    updateGameView(gameRepo.state, gameRepo.remainingShips.value!!)
                    setupBoard(playerOneBoard)
                    gameRepo.getGame(gameID.toString()).removeObservers(this)
            }
        )

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
                    Log.d(TAG,"#SHIPS WAS CHANGED")
                    if(initialShips == gameRepo.remainingShips.value){ //if this player has finished placing their ships
                        gameRepo.state += 1 //increment state (goes to 0 if other player hasn't finished with their ships, 1 otherwise)
                        gameRepo.remainingShips.value = -1
                        placeShips()
                        gameRepo.remainingShips.removeObservers(this)
                    }
                    updateGameView(gameRepo.state, gameRepo.remainingShips.value!!)

                }
            })

        opp_Btn.setOnClickListener {
            setupBoard(playerTwoBoard)
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

        bg = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.gameBackground)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        pressure = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)

        help.setOnClickListener {
            val intent = Intent(this, RulesActivity::class.java)
            startActivity(intent)
        }

        home.setOnClickListener {
            val intent = MainActivity.newIntent(this)
            startActivity(intent)
        }

        gameRepo.changeFlag.observe(
            this,
            Observer {
                if(gameRepo.eventID == gameID && gameRepo.event == "turn"){ //if the other player took their turn
                    gameRepo.state = player
                    loadBoards()
                    setupBoard(playerOneBoard)
                    updateGameView(gameRepo.state, gameRepo.remainingShips.value!!)
                } else if(gameRepo.eventID == gameID && gameRepo.event == "board"){ //if the other player finished placing their ships
                    gameRepo.state += 1
                    if(gameRepo.state != 0){
                        updateGameView(gameRepo.state, gameRepo.remainingShips.value!!)
                    }
                }
            }
        )

    }

    private fun getWhichPlayer(){
        serverInteractions.whichPlayer(deviceID, gameID).observe(
            this,
            Observer { response ->
                response?.let {
                    if(response.get("player").toString() == "1")
                        player = 1
                    else if(response.get("player").toString() == "2")
                        player = 2
                    Log.d(TAG, "player is $player")
                }
                loadBoards()
                serverInteractions.whichPlayer(deviceID, gameID).removeObservers(this)
            }
        )
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
        serverInteractions.makeBoard(gameID, deviceID, board).observe(
            this,
            Observer { response ->
                response?.let {
                    Log.d(TAG,"SENT BOARD: $response")
                }
                serverInteractions.makeBoard(gameID, deviceID, board).removeObservers(this)
            })
        gameRepo.incStatus(gameID.toString())
    }

    private fun loadBoards(){
        serverInteractions.getBoards(gameID).observe(
            this,
            Observer {response ->
                response?.let {
                    Log.d(TAG, "LOADING: $response")
                    gameRepo.state = response.get("turn").toString().toInt()

                    if(response.get("player1").toString() == deviceID){
                        playerOneBoard = parseBoard(response.get("player1_board").toString(), true)
                        playerTwoBoard = parseBoard(response.get("player2_board").toString(), false)
                    } else {
                        playerTwoBoard = parseBoard(response.get("player1_board").toString(), false)
                        playerOneBoard = parseBoard(response.get("player2_board").toString(), true)
                    }
                }
                serverInteractions.getBoards(gameID).removeObservers(this)
            }
        )
    }

    private fun parseBoard(board: String, isThisPlayer: Boolean): MutableList<String>{
        var toReturn: MutableList<String> = mutableListOf(" ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "1")
        val splitBoard = board.split(":")
        var row = 2
        for(i in 1 until splitBoard.size){
            if(i % 3 == 0){
                var value = splitBoard[i][0].toString()
                if(!isThisPlayer && (value=="1")){
                    value = "0"
                }
                toReturn.add(value)
            }

            if(i % 30 == 0 && row < 11){
                toReturn.add(row.toString())
                row++
            }
        }
        return toReturn
    }

    private fun setupBoard (playerBoard: MutableList<String>) {
        gridView = findViewById(R.id.gridview)
        val adapter = SpaceAdapter(this, playerBoard, player)
        gridView.adapter = adapter
    }

    //updates the view based on the state
    private fun updateGameView (state: Int, numShips: Int) {
        Log.d(TAG, "STATE IS: $state")

        when(state){
            (-1) -> {
                userTurn.text = "Place Ships:"+ (initialShips -numShips).toString()
                opp_Btn.visibility= View.INVISIBLE
                my_Btn.visibility= View.INVISIBLE
            }
            0 -> { //placing ships
                userTurn.text = "Waiting for other player to place ships".toString()
                opp_Btn.visibility= View.INVISIBLE
                my_Btn.visibility= View.INVISIBLE
            }
            1 -> {
                //if this player is player one
                if(player == 1){
                    userTurn.text = "Your Turn"
                }
                else{
                    userTurn.text = "Player One's Turn"
                }
                opp_Btn.visibility = View.VISIBLE
                my_Btn.visibility = View.INVISIBLE
            }
            2-> {
                if(player == 2){
                    userTurn.text = "Your Turn"
                }
                else{
                    userTurn.text = "Player Two's Turn"
                }
                my_Btn.visibility = View.VISIBLE
                opp_Btn.visibility = View.INVISIBLE
            }
            else -> {
                userTurn.text = "Turn State is Wrong"
                Log.d( TAG, "something is wrong")
                Log.d( TAG, "ILLEGAL STATE: $state")

            }
        }
    }

    private fun move(position: Int){
        serverInteractions.move(gameID, deviceID, (position % 11)-1, (position / 11)-1).observe(
            this,
            Observer { response ->
                response?.let {
                    Log.d(TAG, "MOVE RESPONSE: $response")
                    gameRepo.state = response.get("turn").toString().toInt()
                    playerTwoBoard[position] = response.get("state").toString()
                    if(response.get("state").toString() == "2"){
                        gameRepo.alternateTurn(gameID.toString())
                    }
                    updateGameView(gameRepo.state, 0)
                    serverInteractions.move(gameID, deviceID, (position % 11)-1, (position / 11)-1).removeObservers(this)
                }
            }
        )
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {

        val light = event.values[0]

        if (light < 20 && settings.use_daylight) {
            bg.rootView.setBackgroundColor(resources.getColor(android.R.color.darker_gray))
        }
        else {
            bg.rootView.setBackgroundColor(resources.getColor(android.R.color.white))
        }
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    companion object{
        fun newIntent(packageContext: Context, id: Int): Intent {
            return Intent(packageContext, GameActivity::class.java).apply {
                putExtra(GAMEID, id)
            }
        }
    }

}