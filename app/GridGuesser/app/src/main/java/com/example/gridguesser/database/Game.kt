package com.example.gridguesser.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity (tableName = "active_games")
data class Game (@PrimaryKey val game_id: Int, var title: String, var red_team: String, var blue_team: String, var red_hits: Int, var blue_hits: Int, var status: Int, var Is_player_1: Int, var hasChanged: Int = 0){}