package com.example.gridguesser.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface SettingsDao {
    @Query("SELECT * FROM settings")
    fun getSettings(): LiveData<List<Settings>>

    @Update
    fun updateSettings(settings: Settings)

    @Insert
    fun addSettings(settings: Settings)
}