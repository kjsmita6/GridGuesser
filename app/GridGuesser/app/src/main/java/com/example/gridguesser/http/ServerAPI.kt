package com.example.gridguesser.http

import retrofit2.Call
import retrofit2.http.GET


interface ServerAPI {
    @GET(".")
    fun getStatus(): Call<String>

    @GET("data/2.5/weather?q=Worcester,us&appid=f8604eb72a4b394e57fe226038fef554")
    fun getWeatherString(): Call<String>
}