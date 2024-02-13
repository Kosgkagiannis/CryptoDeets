package com.cryptodeets.domain.features.marketdata

import javax.inject.Inject

// Delete all data locally
class DeleteAllMarketDataUseCase @Inject constructor(
    private val coinMarketDataRepository: CoinMarketDataRepository
) {

    internal suspend operator fun invoke(): Result<Unit> {
        return coinMarketDataRepository.deleteAllData()
    }

}