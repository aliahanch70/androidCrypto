// path: app/src/main/java/com/example/multisourcecrypto/ui/detail/DetailScreen.kt

package com.example.cryptochart.ui.detail


import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.mikephil.charting.charts.CandleStickChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CandleData
import com.github.mikephil.charting.data.CandleDataSet
import com.github.mikephil.charting.data.CandleEntry
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    coinId: String,
    coinName: String,
    viewModel: DetailViewModel = viewModel()
) {
    val ohlcData by viewModel.ohlcData.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = coinName) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (ohlcData.isNotEmpty()) {
                CandleStickChartComponent(data = ohlcData)
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Chart data is not available.")
                }
            }
        }
    }
}

@Composable
fun CandleStickChartComponent(data: List<com.example.cryptochart.model.OHLCData>) {
    val entries = data.map {
        CandleEntry(
            it.time.toFloat(),
            it.high.toFloat(),
            it.low.toFloat(),
            it.open.toFloat(),
            it.close.toFloat()
        )
    }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp),
        factory = { context ->
            CandleStickChart(context).apply {
                // --- تنظیمات کلی چارت ---
                description.isEnabled = false
                legend.isEnabled = false
                setBackgroundColor(Color.TRANSPARENT) // پس‌زمینه چارت را شفاف می‌کنیم
                setDrawGridBackground(false)

                // --- تنظیمات محور X (زمان) ---
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = Color.WHITE // ✅ رنگ نوشته‌های محور X به سفید تغییر کرد
                    valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                        private val format = SimpleDateFormat("d MMM", Locale.getDefault())
                        override fun getFormattedValue(value: Float): String {
                            return format.format(Date(value.toLong()))
                        }
                    }
                }

                // --- تنظیمات محور Y (قیمت) ---
                axisLeft.apply {
                    setDrawGridLines(true)
                    gridColor = Color.DKGRAY // رنگ خطوط شبکه را خاکستری تیره می‌کنیم
                    textColor = Color.WHITE // ✅ رنگ نوشته‌های محور Y به سفید تغییر کرد
                }
                axisRight.isEnabled = false
            }
        },
        update = { chart ->
            val dataSet = CandleDataSet(entries, "Price Data").apply {
                // --- تنظیمات کندل‌ها ---
                setDrawIcons(false)
                shadowColor = Color.DKGRAY
                shadowWidth = 0.7f

                // ✅ رنگ کندل نزولی (قرمز)
                decreasingColor = Color.RED
                decreasingPaintStyle = Paint.Style.FILL

                // ✅ رنگ کندل صعودی (سبز)
                increasingColor = Color.GREEN
                increasingPaintStyle = Paint.Style.FILL

                neutralColor = Color.LTGRAY // رنگ کندل بدون تغییر
                valueTextColor = Color.TRANSPARENT // قیمت بالای هر کندل را نمایش نمی‌دهیم
            }
            chart.data = CandleData(dataSet)
            chart.invalidate() // بروزرسانی چارت برای نمایش تغییرات
        }
    )
}