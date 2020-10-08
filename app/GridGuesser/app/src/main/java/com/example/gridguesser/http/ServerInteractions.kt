package com.example.gridguesser.http

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "GridGuesser"

class ServerInteractions {

    private val serverAPI: ServerAPI
    private val url: String = "https://68.186.247.90:8080/"
    //private val url: String = "http://192.168.1.192:8080/"
    var serverStatus: LiveData<Boolean>

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        serverAPI = retrofit.create(ServerAPI::class.java)
        serverStatus = checkStatus()
    }

    companion object {
        private var INSTANCE: ServerInteractions? = null

        fun initialize() {
            if (INSTANCE == null) {
                INSTANCE =
                    ServerInteractions()
            }
        }

        fun get(): ServerInteractions {
            return INSTANCE
                ?:
                throw IllegalStateException("CrimeRepository must be initialized")
        }
    }

    fun checkStatus(): LiveData<Boolean> {
        val responseLiveData: MutableLiveData<Boolean> = MutableLiveData()
        val weatherRequest: Call<String> = serverAPI.getStatus()

        weatherRequest.enqueue(object : Callback<String> {

            override fun onFailure(call: Call<String>, t: Throwable) {
                Log.e(TAG, "Failed to get server status", t)
                responseLiveData.value = false
            }

            override fun onResponse(
                call: Call<String>,
                response: Response<String>
            ) {
                responseLiveData.value = true
            }
        })

        return responseLiveData
    }

    fun newGame(title: String, id: String): LiveData<JsonObject> {
        val responseLiveData: MutableLiveData<JsonObject> = MutableLiveData()
        var gameBody = JsonObject()
        gameBody.addProperty("title", title)
        gameBody.addProperty("player1", id)
        val newGameRequest: Call<JsonObject> = serverAPI.getNewGame(gameBody)
        newGameRequest.enqueue(object : Callback<JsonObject> {

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e(TAG, "Failed to create new game", t)
                responseLiveData.value = null
            }

            override fun onResponse(
                call: Call<JsonObject>,
                response: Response<JsonObject>
            ) {
                responseLiveData.value = response.body()
            }
        })

        return responseLiveData
    }
}