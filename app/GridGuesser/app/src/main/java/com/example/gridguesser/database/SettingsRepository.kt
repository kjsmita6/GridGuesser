package com.example.gridguesser.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room

class SettingsRepository private constructor(context: Context) {
    private val database : SettingsDatabase = Room.databaseBuilder(
        context.applicationContext,
        SettingsDatabase::class.java,
        "game-database"
    ).build()

    private val settingsDao = database.settingsDao()

    companion object {
        private var INSTANCE: SettingsRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE =
                    SettingsRepository(context)
            }
        }

        fun get(): SettingsRepository {
            return INSTANCE
                ?:
                throw IllegalStateException("CrimeRepository must be initialized")
        }
    }

    fun getSettings(): LiveData<List<Settings>> = settingsDao.getSettings()

    fun updateSettings(settings: Settings) = settingsDao.updateSettings(settings)

    fun addSettings(settings: Settings) = settingsDao.addSettings(settings)

}