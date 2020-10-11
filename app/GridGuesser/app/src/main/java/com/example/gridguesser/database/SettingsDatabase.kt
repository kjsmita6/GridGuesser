package com.example.gridguesser.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ Settings::class], version=1)
abstract class SettingsDatabase : RoomDatabase() {
    abstract fun settingsDao(): SettingsDao
}