// path: app/src/main/java/com/example/multisourcecrypto/MainActivity.kt
package com.example.cryptochart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.cryptochart.ui.detail.DetailScreen
import com.example.cryptochart.ui.main.MainScreen
import com.example.cryptochart.ui.theme.CryptoChartAlarmTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CryptoChartAlarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = "main_screen"
                    ) {
                        // تعریف صفحه اصلی (لیست)
                        composable("main_screen") {
                            MainScreen(navController = navController)
                        }

                        // تعریف صفحه جزئیات با آرگومان‌های coinId و coinName
                        composable(
                            route = "detail_screen/{coinId}/{coinName}",
                            arguments = listOf(
                                navArgument("coinId") { type = NavType.StringType },
                                navArgument("coinName") { type = NavType.StringType }
                            )
                        ) { backStackEntry ->
                            val coinId = backStackEntry.arguments?.getString("coinId") ?: ""
                            val coinName = backStackEntry.arguments?.getString("coinName") ?: ""
                            DetailScreen(coinId = coinId, coinName = coinName)
                        }
                    }
                }
            }
        }
    }
}