// path: app/src/main/java/com/example/multisourcecrypto/ui/main/MainScreen.kt

package com.example.cryptochart.ui.main

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.cryptochart.data.ApiSource
import com.example.cryptochart.model.Coin
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    navController: NavController,
    viewModel: MainViewModel = viewModel()
) {
    val coins by viewModel.coins.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val selectedApi by viewModel.selectedApi.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Multi-Source Crypto") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            ApiSourceSelector(
                selectedSource = selectedApi,
                onSourceSelected = { viewModel.selectApi(it) }
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(coins, key = { it.id + it.symbol }) { coin ->
                        CoinRow(
                            coin = coin,
                            onItemClick = {
                                navController.navigate("detail_screen/${coin.id}/${coin.name}")
                            }
                        )
                        Divider()
                    }
                }
            }
        }
    }
}

@Composable
fun ApiSourceSelector(
    selectedSource: ApiSource,
    onSourceSelected: (ApiSource) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ApiSource.entries.forEach { source ->
            Button(
                onClick = { onSourceSelected(source) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (source == selectedSource) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = if (source == selectedSource) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            ) {
                Text(source.name)
            }
        }
    }
}


@Composable
fun CoinRow(coin: Coin, onItemClick: () -> Unit) { // <-- Make sure this line is correct
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onItemClick() } // <-- And this line is correct
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = coin.image,
            contentDescription = "${coin.name} logo",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = coin.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(text = coin.symbol.uppercase(), color = Color.Gray, fontSize = 14.sp)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = formatCurrency(coin.current_price),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
            Text(
                text = "${"%.2f".format(coin.price_change_percentage_24h ?: 0.0)}%",
                color = if ((coin.price_change_percentage_24h ?: 0.0) >= 0) Color(0xFF4CAF50) else Color(0xFFF44336),
                fontSize = 14.sp
            )
        }
    }
}

fun formatCurrency(value: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale.US)
    return format.format(value)
}