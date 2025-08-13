// Path: app/src/main/java/com/example/cryptochartalarm/ui/MainScreen.kt
package com.example.cryptochart.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cryptochart.viewmodel.ChartViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: ChartViewModel = viewModel()) {
    val priceEntries = viewModel.getChartEntries()
    val alarmPrice by viewModel.alarmPrice

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crypto Chart Alarm") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AlarmInfoCard(
                alarmPrice = alarmPrice,
                onClearAlarm = { viewModel.clearAlarm() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp)
                    .padding(top = 16.dp)
            ) {
                if (priceEntries.isNotEmpty()) {
                    CryptoChart(
                        entries = priceEntries,
                        alarmPrice = alarmPrice,
                        onValueSelected = { price ->
                            viewModel.setAlarm(price)
                        }
                    )
                } else {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "روی نقطه‌ای از چارت کلیک کنید تا آلارم تنظیم شود.",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun AlarmInfoCard(alarmPrice: Float?, onClearAlarm: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Alarm Price",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = if (alarmPrice != null) "$${"%,.2f".format(alarmPrice)}" else "Not Set",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (alarmPrice != null) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }
            if (alarmPrice != null) {
                Button(onClick = onClearAlarm) {
                    Text("Clear")
                }
            }
        }
    }
}