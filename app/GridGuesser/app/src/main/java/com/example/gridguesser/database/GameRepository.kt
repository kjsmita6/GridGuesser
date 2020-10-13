package com.example.gridguesser.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.gridguesser.deviceID.DeviceID
import java.util.concurrent.Executors

private const val TAG = "GridGuesser"
class GameRepository private constructor(context: Context) {
    var currentSettings: Settings = Settings(0, DeviceID.getDeviceID(context.contentResolver).substring(0, 5), 0, 0)
    private val executor = Executors.newSingleThreadExecutor()

    var state = 0; // 0 - place ships, 1 - player one turn, 2- player two turn
    var ships = MutableLiveData<Int>();

    init{
        ships.value = 0;
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
                throw IllegalStateException("CrimeRepository must be initialized")
        }
    }

    fun getGames(): LiveData<List<Game>> = gameDao.getGames()

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