package com.example.jalasanchaytracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "rainfall_entries")
data class RainfallEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val date: String,
    val rainfallMm: Double,
    val waterSavedLiters: Double
)