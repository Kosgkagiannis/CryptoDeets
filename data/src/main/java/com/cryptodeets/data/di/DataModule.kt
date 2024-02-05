package com.cryptodeets.data.di

import com.cryptodeets.data.features.favouritecoins.FavouriteCoinsLocalDataSource
import com.cryptodeets.data.features.favouritecoins.FavouriteCoinsRepositoryImpl
import com.cryptodeets.data.features.favouritecoins.local.RoomFavouriteCoinsLocalDataSource
import com.cryptodeets.data.features.marketchart.MarketChartRepositoryImpl
import com.cryptodeets.data.features.marketdata.CoinMarketDataLocalDataSource
import com.cryptodeets.data.features.marketdata.CoinMarketDataRemoteDataSource
import com.cryptodeets.data.features.marketdata.CoinMarketDataRepositoryImpl
import com.cryptodeets.data.features.marketdata.local.RoomCoinsMarketDataLocalDataSource
import com.cryptodeets.data.features.marketdata.remote.CoinGeckoCoinMarketDataRemoteDataSource
import com.cryptodeets.data.features.search.SearchRemoteDataSource
import com.cryptodeets.data.features.search.SearchRepositoryImpl
import com.cryptodeets.data.features.search.remote.CoinGeckoSearchRemoteDataSource
import com.cryptodeets.data.features.settings.SettingsLocalDataSource
import com.cryptodeets.data.features.settings.SettingsRepositoryImpl
import com.cryptodeets.data.features.settings.local.RoomSettingsLocalDataSource
import com.cryptodeets.data.features.topcoins.TopCoinsLocalDataSource
import com.cryptodeets.data.features.topcoins.TopCoinsRemoteDataSource
import com.cryptodeets.data.features.topcoins.TopCoinsRepositoryImpl
import com.cryptodeets.data.features.topcoins.local.RoomTopCoinsLocalDataSource
import com.cryptodeets.data.features.topcoins.remote.CoinGeckoTopCoinsRemoteDataSource
import com.cryptodeets.domain.features.favouritecoins.FavouriteCoinsRepository
import com.cryptodeets.domain.features.marketchart.MarketChartRepository
import com.cryptodeets.domain.features.marketdata.CoinMarketDataRepository
import com.cryptodeets.domain.features.search.SearchRepository
import com.cryptodeets.domain.features.settings.SettingsRepository
import com.cryptodeets.domain.features.topcoins.TopCoinsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {


    // Top Coins
    @Binds
    abstract fun bindTopCoinsRepository(topCoinsRepositoryImpl: TopCoinsRepositoryImpl): TopCoinsRepository

    @Binds
    abstract fun bindTopCoinsRemoteDataSource(coinGeckoCoinsRepository: CoinGeckoTopCoinsRemoteDataSource): TopCoinsRemoteDataSource

    @Binds
    abstract fun bindTopCoinsLocalDataSource(roomTopCoinsLocalDataSource: RoomTopCoinsLocalDataSource): TopCoinsLocalDataSource


    // Coin Market Data
    @Binds
    abstract fun bindCoinsMarketDataRepository(coinMarketDataRepositoryImpl: CoinMarketDataRepositoryImpl): CoinMarketDataRepository

    @Binds
    abstract fun bindCoinMarketDataRemoteDataSource(coinGeckoCoinMarketDataRemoteDataSource: CoinGeckoCoinMarketDataRemoteDataSource): CoinMarketDataRemoteDataSource

    @Binds
    abstract fun bindCoinMarketDataLocalDataSource(roomCoinsMarketDataLocalDataSource: RoomCoinsMarketDataLocalDataSource): CoinMarketDataLocalDataSource


}