package com.example.gridguesser.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room

class GameRepository private constructor(context: Context) {
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

}