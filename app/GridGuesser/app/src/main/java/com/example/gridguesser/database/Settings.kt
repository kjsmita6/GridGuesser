package com.example.gridguesser.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class Settings (@PrimaryKey var id: Int, var username: String, var use_temp: Int, var use_daylight: Int){
}