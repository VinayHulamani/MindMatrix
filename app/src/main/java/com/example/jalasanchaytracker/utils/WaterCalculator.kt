package com.example.jalasanchaytracker.utils

object WaterCalculator {

    fun calculateWaterSaved(
        roofArea: Double,
        rainfallMm: Double,
        runoffCoefficient: Double
    ): Double {
        return roofArea * rainfallMm * 0.0929 * runoffCoefficient
    }
}