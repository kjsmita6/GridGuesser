package com.example.gridguesser.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ Game::class ], version=1)
abstract class GameDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
}