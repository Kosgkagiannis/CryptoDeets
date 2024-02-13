package com.cryptodeets.domain.features.marketdata

import com.cryptodeets.domain.features.commons.automaticrefresh.BaseAutomaticRefreshDataFlowRepository
import com.cryptodeets.domain.features.marketdata.models.CoinMarketDataInputParams
import com.cryptodeets.domain.features.marketdata.models.CoinMarketDataRefreshParams
import com.cryptodeets.domain.models.CoinMarketData
import kotlinx.coroutines.flow.Flow

interface CoinMarketDataRepository : BaseAutomaticRefreshDataFlowRepository<CoinMarketData, CoinMarketDataInputParams, CoinMarketDataRefreshParams> {

    override fun getDataFlow(inputParams: CoinMarketDataInputParams): Flow<CoinMarketData>

    override suspend fun refreshData(params: CoinMarketDataRefreshParams): Result<Unit>

    suspend fun deleteAllData(): Result<Unit>

}