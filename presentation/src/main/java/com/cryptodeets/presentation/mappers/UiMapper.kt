package com.cryptodeets.presentation.mappers

import android.os.Build
import androidx.compose.ui.graphics.Color
import com.cryptodeets.domain.exceptions.TemporarilyUnavailableNetworkServiceException
import com.cryptodeets.domain.features.marketchart.models.MarketChartDataPoint
import com.cryptodeets.domain.features.settings.models.GlobalSettingsConfiguration
import com.cryptodeets.domain.features.topcoins.models.TopCoinsData
import com.cryptodeets.domain.models.CoinMarketData
import com.cryptodeets.domain.models.CoinWithMarketData
import com.cryptodeets.domain.models.Currency
import com.cryptodeets.presentation.di.DateAndTimeFormatter
import com.cryptodeets.presentation.di.DateOnlyFormatter
import com.cryptodeets.presentation.di.TimeOnlyFormatter
import com.cryptodeets.presentation.models.*
import com.cryptodeets.presentation.theme.NegativeTrend
import com.cryptodeets.presentation.theme.PositiveTrend
import com.github.davidepanidev.androidextensions.BuildConfig
import com.github.davidepanidev.kotlinextensions.*
import com.github.davidepanidev.kotlinextensions.utils.currencyformatter.CurrencyFormatter
import com.github.davidepanidev.kotlinextensions.utils.numberformatter.NumberFormatter
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.annotations.VisibleForTesting
import timber.log.Timber
import java.io.IOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class UiMapper @Inject constructor(
    private val currencyFormatter: CurrencyFormatter,
    private val numberFormatter: NumberFormatter,
    @DateAndTimeFormatter private val dateTimeFormatter: DateTimeFormatter,
    @DateOnlyFormatter private val dateOnlyFormatter: DateTimeFormatter,
    @TimeOnlyFormatter private val timeOnlyFormatter: DateTimeFormatter,
    private val settingsConfiguration: GlobalSettingsConfiguration
) {


    fun mapTopCoinUiData(topCoinData: TopCoinsData): TopCoinUiData {
        return TopCoinUiData(
            topCoins = mapCoinWithMarketDataUiItemsList(topCoinData.topCoins),
            lastUpdate = with(topCoinData.lastUpdate) {
                if (isToday()) {
                    toFormattedString(timeOnlyFormatter)
                } else {
                    toFormattedString(dateTimeFormatter)
                }
            }
        )
    }

    private fun mapCoinWithMarketDataUiItem(coin: CoinWithMarketData): BaseCoinWithMarketDataUiItem {
        return coin.marketData?.let { marketData ->
            CoinWithMarketDataUiItem(
                id = coin.id,
                name = coin.name,
                symbol = coin.symbol.uppercase(),
                imageUrl = coin.image,
                price = marketData.price?.toFormattedCurrency().orNA(),
                marketCapRank = coin.rank?.toString().orNA(),
                priceChangePercentage = marketData.priceChangePercentage?.formatToPercentage(
                    numberFormatter
                ).orNA(),
                trendColor = marketData.priceChangePercentage?.correspondingTrendColor().orNeutral(),
                sparklineData = coin.marketData?.sparklineData?.mapIndexed { _, d ->
                    DataPoint(y = d, xLabel = null, yLabel = null)
                }?.toImmutableList() ?: persistentListOf(),
                lastUpdate = marketData.lastUpdate.toFormattedString(formatter = dateTimeFormatter)
            )
        } ?: CoinWithShimmeringMarketDataUiItem(
            id = coin.id,
            name = coin.name,
            symbol = coin.symbol.uppercase(),
            imageUrl = coin.image,
            price = "1,000.00$",
            marketCapRank = NA,
            priceChangePercentage = "+0.00%",
            trendColor = Color.Gray,
            sparklineData = persistentListOf(),
            lastUpdate = NOT_AVAILABLE
        )
    }

    fun mapCoinMarketUiData(coinMarketData: CoinMarketData): CoinMarketUiData {
        return with(coinMarketData) {
            CoinMarketUiData(
                price = price?.toFormattedCurrency().orNA(),
                marketDataList = persistentListOf(
                    Pair("Market Info", ""),
                    Pair("Market Cap", marketCap?.toFormattedCurrency().orNotAvailable()),
                    Pair("24 Hour Trading Vol", totalVolume?.toFormattedCurrency().orNotAvailable()),
                    Pair("Supply Info", ""),
                    Pair("Circulating Supply", circulatingSupply?.toFormattedCurrency(withoutSymbol = true).orNotAvailable()),
                    Pair("Total Supply", totalSupply?.toFormattedCurrency(withoutSymbol = true).orNotAvailable()),
                    Pair("Max Supply", totalSupply?.toFormattedCurrency(withoutSymbol = true).orNotAvailable()),
                    Pair("All-Time Info", ""),
                    Pair("All-Time High", ath?.toFormattedCurrency().orNotAvailable()),
                    Pair("All-Time High Date", athDate?.toFormattedString(dateOnlyFormatter).orNotAvailable()),
                    Pair("All-Time Low Price", atl?.toFormattedCurrency().orNotAvailable()),
                    Pair("All-Time Low Date", atlDate?.toFormattedString(dateOnlyFormatter).orNotAvailable()),
                    Pair("Last 24 Hour Info", ""),
                    Pair("Highest Price 24h", high24h?.toFormattedCurrency().orNotAvailable()),
                    Pair("Lowest Price 24h", low24h?.toFormattedCurrency().orNotAvailable()),
                )
            )
        }
    }

    @VisibleForTesting
    internal fun mapCoinWithMarketDataUiItemsList(coinsList: List<CoinWithMarketData>): ImmutableList<BaseCoinWithMarketDataUiItem> {
        return coinsList.map { mapCoinWithMarketDataUiItem(it) }.toImmutableList()
    }

    fun mapMarketChartUiData(marketChartDataPoints: List<MarketChartDataPoint>): MarketChartUiData {
        val startPrice = marketChartDataPoints.first().price
        val lastPrice = marketChartDataPoints.last().price

        val priceChangePercentage: Double = ((lastPrice - startPrice) / startPrice) * 100

        val startPriceDate = marketChartDataPoints.first().date

        val (lowestPrice, lowestPriceDate) = marketChartDataPoints.minBy { it.price }.let {
            Pair(it.price, it.date)
        }

        val (highestPrice, highestPriceDate) = marketChartDataPoints.maxBy { it.price }.let {
            Pair(it.price, it.date)
        }

        return MarketChartUiData(
            chartData = marketChartDataPoints.mapIndexed { _, marketChartDataPoint ->
                DataPoint(
                    y = marketChartDataPoint.price,
                    xLabel = null,
                    yLabel = marketChartDataPoint.price.toFormattedCurrency()
                )
            }.toImmutableList(),
            startPrice = startPrice.toFormattedCurrency(),
            startPriceDate = startPriceDate.toFormattedString(dateOnlyFormatter),
            lowestPrice = lowestPrice.toFormattedCurrency(),
            lowestPriceDate = lowestPriceDate.toFormattedString(dateOnlyFormatter),
            highestPrice = highestPrice.toFormattedCurrency(),
            highestPriceDate = highestPriceDate.toFormattedString(dateOnlyFormatter),
            priceChangePercentage = priceChangePercentage.formatToPercentage(numberFormatter),
            trendColor = priceChangePercentage.correspondingTrendColor()
        )
    }

    fun mapErrorToUiMessage(error: Throwable): String {
        Timber.e("ERROR: $error \n ${error.printStackTrace()}")

        return when(error) {
            is SocketTimeoutException -> "The service is temporarily unavailable. Please try again later."

            is TemporarilyUnavailableNetworkServiceException -> "The ${error.serviceName} service is now at capacity. Please try again in a minute."

            is UnknownHostException,
            is SocketException,
            is IOException -> {
                "It seems you're disconnected from the internet. Kindly verify your connection and try again."
            }

            is retrofit2.HttpException -> {
                when(error.code()) {
                    429, 503 -> "The CoinGecko service is currently not available. Please try again later."
                    in 500..599 -> "The CoinGecko server is not responding. Please try again later."
                    else -> if (BuildConfig.DEBUG) {
                        error.toString()
                    } else {
                        "There has been an error while retrieving data [HTTP ${error.code()}]. Please try again later."
                    }
                }
            }

            else -> if (BuildConfig.DEBUG) {
                error.toString()
            } else {
                "There has been an error while retrieving data. Please try again later."
            }
        }
    }

    private fun Double.correspondingTrendColor(): Color {
        return if (this >= 0) PositiveTrend else NegativeTrend
    }

    private fun Number.toFormattedCurrency(withoutSymbol: Boolean = false): String {
        val number = if (this is Double) {
            this.roundTo2DecimalsIfTooLong()
        } else this

        val symbol = when(settingsConfiguration.getCurrency()) {
            Currency.USD -> "$"
            Currency.EUR -> "€"
            Currency.BTC -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) "₿" else "B"
        }

        val formattedCurrency = number.formatToCurrency(
            currencyFormatter,
            customCurrencySymbol = symbol
        )

        return if (!withoutSymbol) {
            formattedCurrency
        } else {
            formattedCurrency.replace(symbol, "").trim()
        }
    }

    private fun Double.roundTo2DecimalsIfTooLong(): Double {
        return if (this >= 1.1) {
            this.roundToNDecimals(decimals = 2)
        } else {
            this
        }
    }

    private fun String?.orNotAvailable(): String {
        return this ?: NOT_AVAILABLE
    }

    private fun String?.orNA(): String {
        return this ?: NA
    }

    private fun Color?.orNeutral(): Color {
        return this ?: Color.Gray
    }


    companion object {
        const val NOT_AVAILABLE = "Not available"
        const val NA = "N.A."
    }

}