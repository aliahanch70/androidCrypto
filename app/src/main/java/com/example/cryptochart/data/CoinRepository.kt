// path: app/src/main/java/com/example/multisourcecrypto/data/CoinRepository.kt

package com.example.cryptochart.data

import android.util.Log
import com.example.cryptochart.data.network.*
import com.example.cryptochart.model.Coin
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

enum class ApiSource {
    COINGECKO, CRYPTOCOMPARE, BINANCE
}

class CoinRepository {

    private fun createRetrofit(baseUrl: String): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val coinGeckoApi: CoinGeckoApiService = createRetrofit("https://api.coingecko.com/").create(CoinGeckoApiService::class.java)
    private val binanceApi: BinanceApiService = createRetrofit("https://api.binance.com/").create(BinanceApiService::class.java)

    private val cryptoCompareApi: CryptoCompareApiService by lazy {
        val logging = HttpLoggingInterceptor()
        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("authorization", "Apikey 025ba12dc061949ecd8213094fce3554f96bcbd2452695c54086435f0577f0bb")
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .build()

        Retrofit.Builder()
            .baseUrl("https://min-api.cryptocompare.com/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CryptoCompareApiService::class.java)
    }

    suspend fun getTopCoins(source: ApiSource): List<Coin> {
        return try {
            when (source) {
                ApiSource.COINGECKO -> fetchFromCoinGecko()
                ApiSource.CRYPTOCOMPARE -> fetchFromCryptoCompare()
                ApiSource.BINANCE -> fetchFromBinance()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    private suspend fun fetchFromCoinGecko(): List<Coin> {
        val rawCoins = coinGeckoApi.getTopCoins()
        return rawCoins.map {
            Coin(
                id = it.id,
                symbol = it.symbol,
                name = it.name,
                image = it.image,
                current_price = it.currentPrice,
                market_cap = it.marketCap,
                market_cap_rank = it.marketCapRank,
                price_change_percentage_24h = it.priceChangePercentage24h,
                total_volume = it.totalVolume
            )
        }
    }

    private suspend fun fetchFromCryptoCompare(): List<Coin> {
        val response = cryptoCompareApi.getTopCoins()
        return response.Data.mapIndexed { index, item ->
            val raw = item.RAW?.USD
            Coin(
                id = item.CoinInfo.Internal,
                symbol = item.CoinInfo.Name,
                name = item.CoinInfo.FullName,
                image = "https://www.cryptocompare.com${item.CoinInfo.ImageUrl}",
                current_price = raw?.PRICE ?: 0.0,
                market_cap = raw?.MKTCAP ?: 0L,
                market_cap_rank = index + 1,
                price_change_percentage_24h = raw?.CHANGEPCT24HOUR,
                total_volume = raw?.TOTALVOLUME24H
            )
        }
    }

    private suspend fun fetchFromBinance(): List<Coin> {
        val tickers = binanceApi.getTickerPrices()
        return tickers
            .filter { it.symbol.endsWith("USDT") }
            .sortedByDescending { it.quoteVolume.toDoubleOrNull() ?: 0.0 }
            .take(20)
            .mapIndexed { index, ticker ->
                val symbol = ticker.symbol.removeSuffix("USDT")
                Coin(
                    id = symbol.lowercase(),
                    symbol = symbol,
                    name = symbol,
                    image = "https://cryptoicons.org/api/icon/${symbol.lowercase()}/100",
                    current_price = ticker.lastPrice.toDoubleOrNull() ?: 0.0,
                    market_cap = (ticker.quoteVolume.toDoubleOrNull() ?: 0.0).toLong(),
                    market_cap_rank = index + 1,
                    price_change_percentage_24h = ticker.priceChangePercent.toDoubleOrNull(),
                    total_volume = ticker.volume.toDoubleOrNull()
                )
            }
    }

    suspend fun getCoinOHLC(coinId: String, days: String): List<com.example.cryptochart.model.OHLCData> {
        val geckoCompatibleId = when (coinId.lowercase()) {
            "btc" -> "bitcoin"
            "eth" -> "ethereum"
            "bnb" -> "binancecoin"
            "ada" -> "cardano"
            "sol" -> "solana"
            "xrp" -> "ripple"
            "doge" -> "dogecoin"
            else -> coinId
        }

        Log.d("CoinRepository", "Requesting OHLC for ID: $geckoCompatibleId")

        return try {
            // ✅ THE FIX IS HERE
            val response = coinGeckoApi.getCoinOHLC(
                coinId = geckoCompatibleId,
                currency = "usd",
                days = days
            )

            if (response.isEmpty()) {
                Log.w("CoinRepository", "API response for $geckoCompatibleId was empty.")
                return emptyList()
            }
            response.map { item ->
                com.example.cryptochart.model.OHLCData(
                    time = item[0].toLong(),
                    open = item[1],
                    high = item[2],
                    low = item[3],
                    close = item[4]
                )
            }
        } catch (e: Exception) {
            Log.e("CoinRepository", "Failed to fetch OHLC data for ID: $geckoCompatibleId", e)
            emptyList()
        }
    }
}