package com.example.gridguesser

import android.app.Application
import com.example.gridguesser.database.GameRepository
import com.example.gridguesser.http.ServerInteractions

private const val TAG = "GridGuesser"

class GridGuesserApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServerInteractions.initialize()
        GameRepository.initialize(this)
    }
}