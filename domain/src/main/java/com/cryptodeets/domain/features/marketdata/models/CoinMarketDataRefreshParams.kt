package com.cryptodeets.domain.features.marketdata.models

import com.cryptodeets.domain.models.Currency

data class CoinMarketDataRefreshParams(
    val coinId: String,
    val currency: Currency,
)
