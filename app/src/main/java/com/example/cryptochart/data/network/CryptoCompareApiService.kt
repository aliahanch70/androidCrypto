// path: app/src/main/java/com/example/multisourcecrypto/data/network/CryptoCompareApiService.kt
package com.example.cryptochart.data.network

import retrofit2.http.GET
import retrofit2.http.Query

interface CryptoCompareApiService {
    @GET("data/v2/top/mktcapfull")
    suspend fun getTopCoins(
        @Query("limit") limit: Int = 20,
        @Query("tsym") tsym: String = "USD"
    ): CryptoCompareResponse
}

// مدل‌های پاسخ مخصوص CryptoCompare
data class CryptoCompareResponse(val Data: List<CryptoCompareItem>)
data class CryptoCompareItem(
    @JvmField val CoinInfo: CoinInfo,
    @JvmField val RAW: RawData?
)
data class CoinInfo(val Name: String, val FullName: String, val ImageUrl: String, val Internal: String)
data class RawData(val USD: UsdData?)
data class UsdData(
    val PRICE: Double,
    val MKTCAP: Long,
    val CHANGEPCT24HOUR: Double?,
    val TOTALVOLUME24H: Double?
)