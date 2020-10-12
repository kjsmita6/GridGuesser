package com.example.gridguesser

import android.app.Application
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.example.gridguesser.database.GameRepository
import com.example.gridguesser.http.ServerInteractions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception

private const val TAG = "GridGuesser"

class GridGuesserApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ServerInteractions.initialize()
        GameRepository.initialize(this)
    }
}