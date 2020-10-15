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

    @Query("SELECT * FROM active_games WHERE game_id = :id")
    fun getGame(id: String): LiveData<Game>

    @Update
    fun updateGame(game: Game)

    @Query("Update active_games SET blue_team = :uname WHERE game_id = :id")
    fun updateUserName(id: String, uname: String)

    @Query("Update active_games SET status = 0 WHERE game_id = :id")
    fun finishGame(id: String)

    @Query("Update active_games SET red_hits = red_hits + 1 WHERE game_id = :id")
    fun updateScore(id: String)

    @Query("Update active_games SET blue_hits = blue_hits + 1 WHERE game_id = :id")
    fun updateScore2(id: String)

    @Insert
    fun addGame(game: Game)

    @Query("SELECT * FROM settings")
    fun getSettings(): LiveData<List<Settings>>

    @Update
    fun updateSettings(settings: Settings)

    @Insert
    fun addSettings(settings: Settings)
}