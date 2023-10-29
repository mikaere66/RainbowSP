package com.michaelrmossman.kotlin.rainbows.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.michaelrmossman.kotlin.rainbows.dao.RainbowDao
import com.michaelrmossman.kotlin.rainbows.entities.Rainbow

@Database(
    entities = [Rainbow::class],
    version = 1,
    exportSchema = false
)
abstract class RainbowDatabase: RoomDatabase() {
    abstract fun rainbowDao(): RainbowDao
}
