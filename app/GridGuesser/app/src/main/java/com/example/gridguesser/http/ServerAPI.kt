package com.example.gridguesser.http

import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.http.*


interface ServerAPI {
    @GET(".")
    fun getStatus(): Call<String>

    @POST("newgame")
    fun getNewGame(@Body game: JsonObject): Call<JsonObject>

    @POST("joingame")
    fun joinNewGame(@Body game: JsonObject): Call<JsonObject>

    @POST("newuser")
    fun addUser(@Body user: JsonObject): Call<JsonObject>
}