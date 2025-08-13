package com.example.cryptochart.data

import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoApiService {
    @GET("v3/coins/bitcoin/market_chart")
    suspend fun getBitcoinChartData(
        @Query("vs_currency") currency: String = "usd",
        @Query("days") days: Int = 7
    ): MarketChartResponse
}

data class MarketChartResponse(
    val prices: List<List<Float>>
)