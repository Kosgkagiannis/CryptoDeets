package com.cryptodeets.presentation.models

import kotlinx.collections.immutable.ImmutableList

data class TopCoinUiData(
    val topCoins: ImmutableList<BaseCoinWithMarketDataUiItem>,
    val lastUpdate: String
)

data class CoinsListState(
    val topCoinsList: ImmutableList<BaseCoinWithMarketDataUiItem>,
    val lastUpdateDate: String,
    val state: CoinsListUiState
)

sealed interface CoinsListUiState {
    object Idle : CoinsListUiState
    data class Refreshing(val isAutomaticRefresh: Boolean) : CoinsListUiState
    data class Error(val message: String) : CoinsListUiState
}