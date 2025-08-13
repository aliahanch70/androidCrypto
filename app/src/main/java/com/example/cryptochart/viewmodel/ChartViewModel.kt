// Path: app/src/main/java/com/example/cryptochartalarm/viewmodel/ChartViewModel.kt
package com.example.cryptochart.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptochart.data.CryptoApiService
import com.example.cryptochart.data.PricePoint
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChartViewModel : ViewModel() {

    private val apiService: CryptoApiService = Retrofit.Builder()
        .baseUrl("https://api.coingecko.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(CryptoApiService::class.java)

    private val _pricePoints = MutableStateFlow<List<PricePoint>>(emptyList())
    val pricePoints = _pricePoints.asStateFlow()

    val alarmPrice = mutableStateOf<Float?>(null)

    init {
        fetchChartData()
    }

    private fun fetchChartData() {
        viewModelScope.launch {
            try {
                val response = apiService.getBitcoinChartData()
                _pricePoints.value = response.prices.map {
                    PricePoint(timestamp = it[0].toLong(), price = it[1])
                }
            } catch (e: Exception) {
                // در یک پروژه واقعی اینجا باید خطا مدیریت شود
                e.printStackTrace()
            }
        }
    }

    fun setAlarm(price: Float) {
        alarmPrice.value = price
    }

    fun clearAlarm() {
        alarmPrice.value = null
    }

    fun getChartEntries(): List<Entry> {
        return _pricePoints.value.map { Entry(it.timestamp.toFloat(), it.price) }
    }
}