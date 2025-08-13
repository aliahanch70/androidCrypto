// path: app/src/main/java/com/example/multisourcecrypto/model/OHLCData.kt
package com.example.cryptochart.model

data class OHLCData(
    val time: Long,
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double
)