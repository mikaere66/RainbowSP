package com.michaelrmossman.kotlin.rainbows.di

import android.content.Context
import androidx.room.Room
import com.michaelrmossman.kotlin.rainbows.dao.RainbowDao
import com.michaelrmossman.kotlin.rainbows.database.RainbowDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    fun provideRainbowDao(database: RainbowDatabase): RainbowDao {
        return database.rainbowDao()
    }

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext appContext: Context
    ) : RainbowDatabase {
        return Room.databaseBuilder(
            appContext,
            RainbowDatabase::class.java,
            "rainbow_database"
        )
        .createFromAsset("databases/rainbow_database.db")
        //.fallbackToDestructiveMigration()
        .build()
    }
}
