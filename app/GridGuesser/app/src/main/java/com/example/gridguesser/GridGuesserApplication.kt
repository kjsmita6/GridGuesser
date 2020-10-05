package com.example.gridguesser

import android.app.Application
import android.util.Log
import com.example.gridguesser.http.ServerInteractions

private const val TAG = "BasketballCounter"

class GridGuesserApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServerInteractions.initialize()
        Log.d(TAG, "GameRepo Initialized")
    }
}