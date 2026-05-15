package com.example.jalasanchaytracker.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.jalasanchaytracker.data.dao.RainfallDao
import com.example.jalasanchaytracker.data.dao.UserSettingsDao
import com.example.jalasanchaytracker.data.model.RainfallEntry
import com.example.jalasanchaytracker.data.model.UserSettings

@Database(
    entities = [UserSettings::class, RainfallEntry::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userSettingsDao(): UserSettingsDao

    abstract fun rainfallDao(): RainfallDao
}