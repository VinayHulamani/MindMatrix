package com.example.jalasanchaytracker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jalasanchaytracker.data.database.DatabaseProvider
import com.example.jalasanchaytracker.data.repository.AppRepository
import com.example.jalasanchaytracker.ui.theme.JalaSanchayTrackerTheme
import com.example.jalasanchaytracker.viewmodel.AppViewModel
import com.example.jalasanchaytracker.viewmodel.AppViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var repository: AppRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = DatabaseProvider.getDatabase(applicationContext)

        repository = AppRepository(
            database.userSettingsDao(),
            database.rainfallDao()
        )

        setContent {
            val appViewModel: AppViewModel = viewModel(
                factory = AppViewModelFactory(repository)
            )

            JalaSanchayTrackerTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DashboardScreen(appViewModel)
                }
            }
        }
    }
}

@Composable
fun DashboardScreen(appViewModel: AppViewModel) {
    val context = LocalContext.current

    var roofArea by remember { mutableStateOf("") }
    var tankCapacity by remember { mutableStateOf("") }
    var runoffCoefficient by remember { mutableStateOf("0.8") }
    var rainfall by remember { mutableStateOf("") }

    val waterSaved = appViewModel.waterSaved.value
    val history by appViewModel.rainfallHistory.collectAsState()
    val totalSaved by appViewModel.totalWaterSaved.collectAsState()
    val monthlySaved by appViewModel.monthlyWaterSaved.collectAsState()
    val monthlyEntries by appViewModel.monthlyEntries.collectAsState()

    val waterDays = (totalSaved / 135).toInt()

    val tankProgress =
        if (appViewModel.savedTankCapacity.value > 0)
            (waterSaved / appViewModel.savedTankCapacity.value)
                .coerceIn(0.0, 1.0)
                .toFloat()
        else 0f

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Text(
                "Jala Sanchay Tracker",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Today's Savings: ${waterSaved.toInt()} Liters")
                    Text("Total Savings: ${totalSaved.toInt()} Liters")
                    Text("Household Water Days: $waterDays days")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Monthly Report",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Monthly Water Saved: ${monthlySaved.toInt()} Liters")
                    Text("Rainfall Entries This Month: $monthlyEntries")

                    if (monthlyEntries > 0) {
                        Text(
                            "Average Per Entry: ${
                                (monthlySaved / monthlyEntries).toInt()
                            } Liters"
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Water Tank Level",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .height(200.dp)
                    .width(120.dp)
                    .border(2.dp, Color.Gray, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.BottomCenter
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(tankProgress)
                        .background(
                            Color.Cyan,
                            RoundedCornerShape(16.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = roofArea,
                onValueChange = { roofArea = it },
                label = { Text("Roof Area") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = tankCapacity,
                onValueChange = { tankCapacity = it },
                label = { Text("Tank Capacity") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = runoffCoefficient,
                onValueChange = { runoffCoefficient = it },
                label = { Text("Runoff Coefficient") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    try {
                        appViewModel.saveUserSettings(
                            roofArea.toDouble(),
                            tankCapacity.toDouble(),
                            runoffCoefficient.toDouble()
                        )

                        Toast.makeText(
                            context,
                            "Settings Saved",
                            Toast.LENGTH_SHORT
                        ).show()
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Enter valid values",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Settings")
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = rainfall,
                onValueChange = { rainfall = it },
                label = { Text("Rainfall (mm)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    try {
                        appViewModel.saveRainfall(rainfall.toDouble())
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Enter rainfall",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate Water")
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Water Saving Tips",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("• Clean rooftop monthly")
                    Text("• Check tank leakage regularly")
                    Text("• Use first flush diverter")
                    Text("• Cover tank to avoid contamination")
                    Text("• Reuse stored water for gardening")
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                "Rainfall History",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(8.dp))
        }

        items(history) { entry ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Date: ${entry.date}")
                    Text("Rainfall: ${entry.rainfallMm} mm")
                    Text("Saved: ${entry.waterSavedLiters.toInt()} Liters")
                }
            }
        }
    }
}