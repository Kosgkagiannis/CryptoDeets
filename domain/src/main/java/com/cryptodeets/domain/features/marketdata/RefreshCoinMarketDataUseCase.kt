package com.cryptodeets.domain.features.marketdata

import com.cryptodeets.domain.features.commons.automaticrefresh.BaseRefreshDataUseCase
import com.cryptodeets.domain.features.marketdata.models.CoinMarketDataInputParams
import com.cryptodeets.domain.features.marketdata.models.CoinMarketDataRefreshParams
import com.cryptodeets.domain.features.settings.models.GlobalSettingsConfiguration
import com.cryptodeets.domain.models.CoinMarketData
import javax.inject.Inject

// UseCase for refreshing data
class RefreshCoinMarketDataUseCase @Inject constructor(
    coinMarketDataRepository: CoinMarketDataRepository,
    private val settingsConfiguration: GlobalSettingsConfiguration
) : BaseRefreshDataUseCase<CoinMarketData, CoinMarketDataInputParams, CoinMarketDataRefreshParams>(
    repository = coinMarketDataRepository
) {

    override fun getRefreshParams(inputParams: CoinMarketDataInputParams): CoinMarketDataRefreshParams {
        val settings = settingsConfiguration

        return CoinMarketDataRefreshParams(
            coinId = inputParams.coinId,
            currency = settings.currency
        )
    }

}