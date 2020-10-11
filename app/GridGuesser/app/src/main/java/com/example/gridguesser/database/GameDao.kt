package com.example.gridguesser.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface GameDao {
    @Query("SELECT * FROM active_games")
    fun getGames(): LiveData<List<Game>>

    @Update
    fun updateGame(game: Game)

    @Insert
    fun addGame(game: Game)

    @Query("SELECT * FROM settings")
    fun getSettings(): LiveData<List<Settings>>

    @Update
    fun updateSettings(settings: Settings)

    @Insert
    fun addSettings(settings: Settings)
}