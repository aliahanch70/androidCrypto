// path: app/src/main/java/com/example/multisourcecrypto/data/network/BinanceApiService.kt
package com.example.cryptochart.data.network

import retrofit2.http.GET

interface BinanceApiService {
    @GET("api/v3/ticker/24hr")
    suspend fun getTickerPrices(): List<BinanceTicker>
}

// مدل پاسخ مخصوص Binance
data class BinanceTicker(
    val symbol: String,
    val lastPrice: String,
    val quoteVolume: String,
    val priceChangePercent: String,
    val volume: String
)