package com.example.gridguesser.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.gridguesser.deviceID.DeviceID
import java.util.concurrent.Executors

private const val TAG = "GridGuesser"
class GameRepository private constructor(var context: Context) {
    var currentSettings: Settings = Settings(0, DeviceID.getDeviceID(context.contentResolver).substring(0, 5), false)
    private val executor = Executors.newSingleThreadExecutor()

    var state = 0 // 0 - place ships, 1 - player one turn, 2- player two turn
    var id = -1
    var remainingShips = MutableLiveData<Int>()
    var currentGame = Game(-1, "", "", "", -1, -1, -1, -1, -1)

    var changeFlag = MutableLiveData<Boolean>(false)
    var event: String = ""
    var eventID: Int = -1

    init{
        remainingShips.value = 0
    }

    private val database : GameDatabase = Room.databaseBuilder(
        context.applicationContext,
        GameDatabase::class.java,
        "game-database"
    ).build()

    private val gameDao = database.gameDao()

    companion object {
        private var INSTANCE: GameRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE =
                    GameRepository(context)
            }

        }

        fun get(): GameRepository {
            return INSTANCE
                ?:
                throw IllegalStateException("GameRepository must be initialized")
        }
    }

    fun updateEvent(e: String, gameID: String){
        event = e
        eventID = gameID.toInt()
        changeFlag.postValue(true)
    }

    fun getGames(): LiveData<List<Game>> = gameDao.getGames()

    fun getGame(id: String): LiveData<Game> = gameDao.getGame(id)

    fun updateUserName(id: String, uname: String)  {
        executor.execute {
            gameDao.updateUserName(id, uname)
        }
    }

    fun updateScore(id: String, player1: Boolean) {
        if(player1) {
            if(id == currentGame.game_id.toString()){
                currentGame.red_hits++
            }
            executor.execute {
                gameDao.updateScore(id)
            }
        } else {
            gameDao.updateScore2(id)
        }
    }

    fun incStatus(id: String)  {
        if(id == currentGame.game_id.toString()){
            currentGame.status++
        }
        executor.execute {
            gameDao.incStatus(id)
        }
    }

    fun notifyChange(id: String, notify: Boolean)  {
        executor.execute {
            if(notify){
                gameDao.gameChange(id, 1)
            } else {
                gameDao.gameChange(id, 0)
            }
        }
    }

    fun alternateTurn(id: String)  {
        executor.execute {
            gameDao.alternateTurn(id)
        }
    }

    fun finishGame(id: String) {
        executor.execute {
            gameDao.finishGame(id)
        }
    }

    fun updateGame(game: Game) {
        executor.execute {
            gameDao.updateGame(game)
        }
    }

    fun addGame(game: Game) {
        executor.execute {
            gameDao.addGame(game)
        }
    }

    fun getSettings(): LiveData<List<Settings>> = gameDao.getSettings()

    fun updateSettings(settings: Settings) {
        executor.execute {
            gameDao.updateSettings(settings)
        }
    }

    fun addSettings(settings: Settings){
        executor.execute {
            gameDao.addSettings(settings)
        }
    }

}