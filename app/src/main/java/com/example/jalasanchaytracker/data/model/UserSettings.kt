package com.example.jalasanchaytracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettings(
    @PrimaryKey val id: Int = 1,
    val roofArea: Double,
    val tankCapacity: Double,
    val runoffCoefficient: Double
)