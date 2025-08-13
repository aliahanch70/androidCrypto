// path: app/src/main/java/com/example/cryptochart/ui/main/MainViewModel.kt
package com.example.cryptochart.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cryptochart.data.ApiSource
import com.example.cryptochart.data.CoinRepository
import com.example.cryptochart.model.Coin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repository = CoinRepository()

    private val _coins = MutableStateFlow<List<Coin>>(emptyList())
    val coins: StateFlow<List<Coin>> = _coins

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedApi = MutableStateFlow(ApiSource.COINGECKO)
    val selectedApi: StateFlow<ApiSource> = _selectedApi

    init {
        loadCoins()
    }

    fun selectApi(source: ApiSource) {
        _selectedApi.value = source
        loadCoins()
    }

    private fun loadCoins() {
        viewModelScope.launch {
            _isLoading.value = true
            _coins.value = emptyList() // پاک کردن لیست قبلی
            val result = repository.getTopCoins(_selectedApi.value)
            _coins.value = result
            _isLoading.value = false
        }
    }
}