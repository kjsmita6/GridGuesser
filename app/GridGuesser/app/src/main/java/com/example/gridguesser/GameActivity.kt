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
    private var displayedBoard: Int = 1

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

    override fun assignShip(position: Int) {
        playerOneBoard[position] = "1"
        Log.d(TAG, "PLAYER ONE SET $position")
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
        gameRepo.notifyChange(gameID.toString(), false)

        gameRepo.remainingShips.value = 0

        getWhichPlayer()

        var observeState = true
        gameRepo.getGame(gameID.toString()).observe(
            this,
            Observer { thisGame ->
                if(observeState){
                    observeState = false;
                    gameRepo.state = thisGame.status
                    Log.d(TAG, "INITIAL STATUS: ${gameRepo.state}")
                    updateGameView(gameRepo.state, gameRepo.remainingShips.value!!)
                    setupBoard(playerOneBoard, 1)
                }
            }
        )

        opp_Btn = findViewById(R.id.goToOpponent)
        my_Btn = findViewById(R.id.goToPlayer)
        userTurn = findViewById(R.id.userTurn)
        boardTitle = findViewById(R.id.boardTitle)
        help = findViewById(R.id.help)
        home = findViewById(R.id.home)

        updateGameView(gameRepo.state, gameRepo.remainingShips.value!!)

        var observeShips = true
        gameRepo.remainingShips.observe(
        this,
        Observer { ships ->
            ships?.let {
                if (observeShips){
                    Log.d(TAG,"#SHIPS WAS CHANGED")
                    if(initialShips == gameRepo.remainingShips.value){ //if this player has finished placing their ships
                        gameRepo.state += 1 //increment state (goes to 0 if other player hasn't finished with their ships, 1 otherwise)
                        gameRepo.remainingShips.value = 0
                        placeShips()
                        observeShips = false
                    }
                    updateGameView(gameRepo.state, gameRepo.remainingShips.value!!)
                }
            }
        })

        opp_Btn.setOnClickListener {
            setupBoard(playerTwoBoard, 2)
            my_Btn.visibility= View.VISIBLE
            opp_Btn.visibility= View.INVISIBLE
            boardTitle.text = resources.getString(R.string.opponents_ships)
        }

        my_Btn.setOnClickListener {
            setupBoard(playerOneBoard, 1)
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
                    updateGameView(gameRepo.state, gameRepo.remainingShips.value!!)
                    if(displayedBoard == 1){
                        setupBoard(playerOneBoard, 1)
                    }
                    gameRepo.notifyChange(gameID.toString(), false)
                } else if(gameRepo.eventID == gameID && gameRepo.event == "board"){ //if the other player finished placing their ships
                    gameRepo.state += 1
                    if(gameRepo.state != 0){
                        updateGameView(gameRepo.state, gameRepo.remainingShips.value!!)
                    }
                    gameRepo.notifyChange(gameID.toString(), false)
                }
            }
        )

    }

    private fun getWhichPlayer(){
        var observePlayer = true
        serverInteractions.whichPlayer(deviceID, gameID).observe(
            this,
            Observer { response ->
                if(observePlayer){
                    observePlayer = false
                    response?.let {
                        if(response.get("player").toString() == "1")
                            player = 1
                        else if(response.get("player").toString() == "2")
                            player = 2
                        Log.d(TAG, "player is $player")
                    }
                    loadBoards()
                }
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
                Log.d(TAG, "AT $i, $j: ${playerOneBoard[11*i+j]}")
            }

            board += if(i == 10){
                "]"
            } else {
                "],"
            }
        }
        board += "]"

        var observeBoard = true
        serverInteractions.makeBoard(gameID, deviceID, board).observe(
            this,
            Observer { response ->
                if(observeBoard){
                    observeBoard = false
                    response?.let {
                        Log.d(TAG,"SENT BOARD: $response")
                    }
                }
            })
        gameRepo.incStatus(gameID.toString())
    }

    private fun loadBoards(){
        var observeLoad = true
        serverInteractions.getBoards(gameID).observe(
            this,
            Observer {response ->
                if(observeLoad){
                    observeLoad = false;
                    response?.let {
                        Log.d(TAG, "LOADING: $response")
                        gameRepo.state = response.get("turn").toString().toInt()

                        Log.d(TAG, "${response.get("player1")} AND D-ID $deviceID")
                        if(response.get("player1").toString().replace("\"", "") == deviceID){
                            playerOneBoard = parseBoard(response.get("player1_board").toString(), true)
                            playerTwoBoard = parseBoard(response.get("player2_board").toString(), false)
                        } else {
                            playerTwoBoard = parseBoard(response.get("player1_board").toString(), false)
                            playerOneBoard = parseBoard(response.get("player2_board").toString(), true)
                        }
                    }
                }
            }
        )
    }


    //change server board style to be the gridview style
    private fun parseBoard(board: String, isThisPlayer: Boolean): MutableList<String>{
        var toReturn: MutableList<String> = mutableListOf(" ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "1")
        val splitBoard = board.split(":")
        var row = 2
        for(i in 1 until splitBoard.size){
            if(i % 3 == 0){
                var value = splitBoard[i][0].toString()
                if(!isThisPlayer && value=="1"){ //don't show opponents ships
                    Log.d(TAG, "FOUND OPPONENT SHIP AT ${toReturn.size-1}")
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

    private fun setupBoard (playerBoard: MutableList<String>, whichBoard: Int) {
        displayedBoard = whichBoard
        gridView = findViewById(R.id.gridview)
        val adapter = SpaceAdapter(this, playerBoard, player, whichBoard)
        gridView.adapter = adapter
    }

    //updates the view based on the state
    private fun updateGameView (state: Int, numShips: Int) {
        Log.d(TAG, "STATE IS: $state")

        when(state){
            (-1) -> {
                userTurn.text = "Place Ships:"+ (initialShips -numShips).toString()
                boardTitle.text = resources.getString(R.string.your_ships)
                opp_Btn.visibility= View.INVISIBLE
                my_Btn.visibility= View.INVISIBLE
            }
            0 -> { //placing ships
                userTurn.text = "Waiting for other player to place ships".toString()
                boardTitle.text = resources.getString(R.string.your_ships)
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
        var observeMove = true
        serverInteractions.move(gameID, deviceID, (position / 11)-1, (position % 11)-1).observe(
            this,
            Observer { response ->
                if(observeMove){
                    observeMove = false
                    response?.let {
                        Log.d(TAG, "MOVE RESPONSE: $response")
                        gameRepo.state = response.get("turn").toString().toInt()
                        playerTwoBoard[position] = response.get("state").toString()
                        if(response.get("state").toString() == "3"){
                            gameRepo.updateScore(gameID.toString(), true)
                        }
                        gameRepo.alternateTurn(gameID.toString())
                        updateGameView(gameRepo.state, 0)
                        if(displayedBoard == 2){
                            setupBoard(playerTwoBoard, 2)
                        }
                    }
                }
            }
        )
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {

        val light = event.values[0]

        if (light < 20 && settings.use_daylight) {
            bg.rootView.setBackgroundColor(resources.getColor(R.color.colorPrimary))
        }
        else {
            bg.rootView.setBackgroundColor(resources.getColor(R.color.colorSecondary))
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