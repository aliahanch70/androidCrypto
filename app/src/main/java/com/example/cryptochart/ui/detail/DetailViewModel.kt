// path: app/src/main/java/com/example/multisourcecrypto/ui/detail/DetailViewModel.kt
package com.example.cryptochart.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptochart.data.CoinRepository
import com.example.cryptochart.model.OHLCData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailViewModel(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val repository = CoinRepository()
    private val coinId: String = checkNotNull(savedStateHandle["coinId"])

    private val _ohlcData = MutableStateFlow<List<OHLCData>>(emptyList())
    val ohlcData: StateFlow<List<OHLCData>> = _ohlcData

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadChartData("7") // به صورت پیش‌فرض چارت ۷ روزه را لود می‌کنیم
    }

    fun loadChartData(days: String) {
        viewModelScope.launch {
            _isLoading.value = true
            // متد getCoinOHLC را به Repository اضافه خواهیم کرد
            val result = repository.getCoinOHLC(coinId, days)
            _ohlcData.value = result
            _isLoading.value = false
        }
    }
}