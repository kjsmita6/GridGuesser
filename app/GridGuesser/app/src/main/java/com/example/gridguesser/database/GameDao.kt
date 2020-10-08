package com.example.gridguesser.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gridguesser.database.Game
import java.util.*

@Dao
interface GameDao {
    @Query("SELECT * FROM active_games")
    fun getGames(): LiveData<List<Game>>

    @Update
    fun updateGame(game: Game)

    @Insert
    fun addGame(game: Game)
}