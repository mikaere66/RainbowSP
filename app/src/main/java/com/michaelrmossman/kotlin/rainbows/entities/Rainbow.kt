package com.michaelrmossman.kotlin.rainbows.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rainbows_table")
data class Rainbow(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo
    var id: Int = 0,

    @ColumnInfo
    val groupNum: Int,

    @ColumnInfo
    val ledNum: Int,

    @ColumnInfo
    val ledCurrent: Int,

    @ColumnInfo
    val rgbValue: String
)
