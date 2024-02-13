package com.cryptodeets.domain.features.marketdata

import com.cryptodeets.domain.features.commons.automaticrefresh.BaseAutomaticRefreshDataFlowUseCase
import com.cryptodeets.domain.features.marketdata.models.CoinMarketDataInputParams
import com.cryptodeets.domain.features.marketdata.models.CoinMarketDataRefreshParams
import com.cryptodeets.domain.models.COIN_MARKET_DATA_REFRESH_PERIOD
import com.cryptodeets.domain.models.CoinMarketData
import javax.inject.Inject

// Get data flow usecase
class GetCoinMarketDataFlowUseCase @Inject constructor(
    coinMarketDataRepository: CoinMarketDataRepository,
    refreshCoinMarketDataUseCase: RefreshCoinMarketDataUseCase
) : BaseAutomaticRefreshDataFlowUseCase<CoinMarketData, CoinMarketDataInputParams, CoinMarketDataRefreshParams>(
    refreshDataUseCase = refreshCoinMarketDataUseCase,
    repository = coinMarketDataRepository,
    minutesRequiredToRefreshData = COIN_MARKET_DATA_REFRESH_PERIOD.toLong()
)