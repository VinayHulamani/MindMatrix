package com.example.jalasanchaytracker.data.repository

import com.example.jalasanchaytracker.data.dao.RainfallDao
import com.example.jalasanchaytracker.data.dao.UserSettingsDao
import com.example.jalasanchaytracker.data.model.RainfallEntry
import com.example.jalasanchaytracker.data.model.UserSettings
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val userSettingsDao: UserSettingsDao,
    private val rainfallDao: RainfallDao
) {

    suspend fun saveSettings(settings: UserSettings) {
        userSettingsDao.saveSettings(settings)
    }

    suspend fun getSettings(): UserSettings? {
        return userSettingsDao.getSettings()
    }

    suspend fun insertRainfall(entry: RainfallEntry) {
        rainfallDao.insertEntry(entry)
    }

    fun getAllEntries(): Flow<List<RainfallEntry>> {
        return rainfallDao.getAllEntries()
    }

    fun getTotalWaterSaved(): Flow<Double?> {
        return rainfallDao.getTotalWaterSaved()
    }
}