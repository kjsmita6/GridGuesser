package com.example.gridguesser.http

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ServerAPI {
    @GET(".")
    fun getStatus(): Call<String>

    @GET("/newgame")
    fun getNewGame(@Query("title") Title: String, @Query("player1") id: String): Call<String>
}