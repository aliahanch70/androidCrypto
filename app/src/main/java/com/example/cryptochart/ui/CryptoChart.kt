package com.example.cryptochart.ui

import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.github.mikephil.charting.utils.MPPointF
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CryptoChart(
    entries: List<Entry>,
    alarmPrice: Float?,
    onValueSelected: (Float) -> Unit
) {
    val context = LocalContext.current

    // فرمت‌دهی تاریخ برای محور X
    val dateFormatter = remember { SimpleDateFormat("MMM dd", Locale.getDefault()) }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { ctx ->
            LineChart(ctx).apply {
                // --- تنظیمات کلی چارت ---
                description.isEnabled = false
                isDragEnabled = true
                setScaleEnabled(true)
                setPinchZoom(true)
                setDrawGridBackground(false)
                legend.isEnabled = false

                // --- محور X (زمان) ---
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    textColor = Color.WHITE // Assuming this is inside a dark theme; otherwise, use Color.BLACK

                    // ✅ از object استفاده کنید تا یک پیاده‌سازی بی‌نام بسازید
                    valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            // منطق فرمت‌دهی شما در اینجا قرار می‌گیرد
                            return dateFormatter.format(Date(value.toLong()))
                        }
                    }
                }

                // --- محور Y (قیمت) ---
                axisLeft.apply {
                    textColor = Color.WHITE
                    setDrawGridLines(true)
                    gridColor = Color.DKGRAY
                }
                axisRight.isEnabled = false

                // --- تنظیمات Listener برای انتخاب نقطه ---
                setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
                    override fun onValueSelected(e: Entry?, h: Highlight?) {
                        e?.let {
                            onValueSelected(it.y)
                        }
                    }

                    override fun onNothingSelected() {}
                })
            }
        },
        update = { chart ->
            // --- ایجاد دیتاست (DataSet) ---
            val dataSet = LineDataSet(entries, "Bitcoin Price").apply {
                color = Color.CYAN
                valueTextColor = Color.TRANSPARENT
                setDrawCircles(false)
                lineWidth = 2f
                setDrawFilled(true)
                fillColor = Color.CYAN
                fillAlpha = 50
            }

            chart.data = LineData(dataSet)

            // --- اضافه کردن خط آلارم (LimitLine) ---
            chart.axisLeft.removeAllLimitLines() // پاک کردن خطوط قبلی
            alarmPrice?.let { price ->
                val limitLine = com.github.mikephil.charting.components.LimitLine(price, "Alarm: $${"%,.2f".format(price)}").apply {
                    lineWidth = 2f
                    lineColor = Color.RED
                    textColor = Color.WHITE
                    textSize = 12f
                    enableDashedLine(10f, 10f, 0f)
                    labelPosition = com.github.mikephil.charting.components.LimitLine.LimitLabelPosition.RIGHT_TOP
                }
                chart.axisLeft.addLimitLine(limitLine)
            }

            // بروزرسانی چارت
            chart.invalidate()
        }
    )
}