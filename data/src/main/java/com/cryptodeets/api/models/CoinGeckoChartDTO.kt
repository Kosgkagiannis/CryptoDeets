package com.cryptodeets.data.api.coingecko.models


import com.google.gson.annotations.SerializedName

data class CoinGeckoChartDTO(
    val prices: List<List<Double>>,
    @SerializedName("market_caps")
    val marketCaps: List<List<Double>>,
    @SerializedName("total_volumes")
    val totalVolumes: List<List<Double>>
)