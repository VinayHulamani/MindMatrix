package com.example.jalasanchaytracker.data.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.jalasanchaytracker.data.model.RainfallEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface RainfallDao {

    @Insert
    suspend fun insertEntry(entry: RainfallEntry)

    @Query("SELECT * FROM rainfall_entries ORDER BY id DESC")
    fun getAllEntries(): Flow<List<RainfallEntry>>

    @Query("SELECT SUM(waterSavedLiters) FROM rainfall_entries")
    fun getTotalWaterSaved(): Flow<Double?>
}