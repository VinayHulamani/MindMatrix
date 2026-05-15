package com.example.jalasanchaytracker.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jalasanchaytracker.data.model.RainfallEntry
import com.example.jalasanchaytracker.data.model.UserSettings
import com.example.jalasanchaytracker.data.repository.AppRepository
import com.example.jalasanchaytracker.utils.WaterCalculator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AppViewModel(
    private val repository: AppRepository
) : ViewModel() {

    var waterSaved = mutableStateOf(0.0)

    var savedRoofArea = mutableStateOf(0.0)
    var savedTankCapacity = mutableStateOf(0.0)
    var savedRunoffCoefficient = mutableStateOf(0.8)

    private val _rainfallHistory = MutableStateFlow<List<RainfallEntry>>(emptyList())
    val rainfallHistory: StateFlow<List<RainfallEntry>> = _rainfallHistory

    private val _totalWaterSaved = MutableStateFlow(0.0)
    val totalWaterSaved: StateFlow<Double> = _totalWaterSaved

    private val _monthlyWaterSaved = MutableStateFlow(0.0)
    val monthlyWaterSaved: StateFlow<Double> = _monthlyWaterSaved

    private val _monthlyEntries = MutableStateFlow(0)
    val monthlyEntries: StateFlow<Int> = _monthlyEntries

    init {
        viewModelScope.launch {
            repository.getAllEntries().collect { entries ->
                _rainfallHistory.value = entries
                _totalWaterSaved.value = entries.sumOf { it.waterSavedLiters }

                val currentMonth =
                    SimpleDateFormat("MMM yyyy", Locale.getDefault())
                        .format(Date())

                val monthlyList = entries.filter {
                    it.date.contains(currentMonth)
                }

                _monthlyWaterSaved.value =
                    monthlyList.sumOf { it.waterSavedLiters }

                _monthlyEntries.value = monthlyList.size
            }
        }
    }

    fun saveUserSettings(
        roofArea: Double,
        tankCapacity: Double,
        runoffCoefficient: Double
    ) {
        savedRoofArea.value = roofArea
        savedTankCapacity.value = tankCapacity
        savedRunoffCoefficient.value = runoffCoefficient

        viewModelScope.launch {
            repository.saveSettings(
                UserSettings(
                    roofArea = roofArea,
                    tankCapacity = tankCapacity,
                    runoffCoefficient = runoffCoefficient
                )
            )
        }
    }

    fun saveRainfall(rainfall: Double) {
        val water = WaterCalculator.calculateWaterSaved(
            savedRoofArea.value,
            rainfall,
            savedRunoffCoefficient.value
        )

        waterSaved.value = water

        val formattedDate =
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
                .format(Date())

        viewModelScope.launch {
            repository.insertRainfall(
                RainfallEntry(
                    date = formattedDate,
                    rainfallMm = rainfall,
                    waterSavedLiters = water
                )
            )
        }
    }
}