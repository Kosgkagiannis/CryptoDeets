package com.cryptodeets.data.api.coingecko

import com.cryptodeets.data.api.coingecko.models.CoinGeckoChartDTO
import com.cryptodeets.data.api.coingecko.models.CoinGeckoMarketsDto
import com.cryptodeets.data.api.coingecko.models.CoinGeckoSearchDTO
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


interface CoinGeckoApi {

    companion object {
        const val API_URL = "https://api.coingecko.com/api/v3/"
    }

    @GET("coins/markets")
    suspend fun getCoinsMarkets(
        @Query("vs_currency") currency: String = "usd",
        @Query("page") page: Int = 1,
        @Query("per_page") numCoinsPerPage: Int = 100,
        @Query("order") order: String = "market_cap_desc",
        @Query("sparkline") includeSparkline7dData: Boolean = false,
        @Query("price_change_percentage") priceChangePercentageIntervals: String = "",
        @Query("ids") coinIds: String? = null
    ): List<CoinGeckoMarketsDto>

    @GET("coins/{id}/market_chart")
    suspend fun getCoinMarketChart(
        @Path("id") coinId: String,
        @Query("vs_currency") currency: String = "usd",
        @Query("days") days: String = "1"
    ): CoinGeckoChartDTO

    @GET("search")
    suspend fun getSearch(
        @Query("query") query: String
    ): CoinGeckoSearchDTO




}