package com.cryptodeets.data.db.room

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.cryptodeets.data.db.room.models.CoinMarketDataEntity
import com.cryptodeets.data.features.favouritecoins.local.FavouriteCoinsDao
import com.cryptodeets.data.features.favouritecoins.local.models.FavouriteCoinEntity
import com.cryptodeets.data.features.marketdata.local.CoinsMarketDataDao
import com.cryptodeets.data.features.settings.local.SettingsDao
import com.cryptodeets.data.features.settings.local.models.SettingsEntity
import com.cryptodeets.data.features.topcoins.local.TopCoinsDao
import com.cryptodeets.data.features.topcoins.local.models.TopCoinEntity

@Database(
    entities = [
        TopCoinEntity::class,
        CoinMarketDataEntity::class,
    ],
    version = 4,
    exportSchema = true,
    autoMigrations = [
        AutoMigration (from = 1, to = 2),
        AutoMigration (from = 2, to = 3),
        AutoMigration(from = 3, to = 4)
    ]
)
abstract class CoinsDatabase : RoomDatabase() {

    abstract fun topCoinsDao(): TopCoinsDao
    abstract fun coinsMarketDataDao(): CoinsMarketDataDao
}