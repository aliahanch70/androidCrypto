// path: app/src/main/java/com/example/multisourcecrypto/data/network/CoinGeckoApiService.kt

package com.example.cryptochart.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// This interface defines the endpoints for the CoinGecko API.
interface CoinGeckoApiService {

    /**
     * Fetches a list of top coins from the market.
     */
    @GET("api/v3/coins/markets")
    suspend fun getTopCoins(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1
    ): List<CoinGeckoCoin>

    /**
     * Fetches the OHLC (Open, High, Low, Close) data for a specific coin.
     * This is used to draw the candlestick chart.
     */
    @GET("api/v3/coins/{id}/ohlc")
    suspend fun getCoinOHLC(
        @Path("id") coinId: String,
        @Query("vs_currency") currency: String,
        @Query("days") days: String
    ): List<List<Double>>
}

/**
 * This is the data model that matches the JSON response from the /coins/markets endpoint.
 * We use @SerializedName to map the snake_case JSON keys to our camelCase variable names.
 */
data class CoinGeckoCoin(
    val id: String,
    val symbol: String,
    val name: String,
    val image: String,
    @SerializedName("current_price")
    val currentPrice: Double,
    @SerializedName("market_cap")
    val marketCap: Long,
    @SerializedName("market_cap_rank")
    val marketCapRank: Int,
    @SerializedName("price_change_percentage_24h")
    val priceChangePercentage24h: Double?,
    @SerializedName("total_volume")
    val totalVolume: Double?
)