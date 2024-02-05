package com.cryptodeets.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.cryptodeets.data.db.room.CoinsDatabase
import com.cryptodeets.data.features.favouritecoins.local.FavouriteCoinsDao
import com.cryptodeets.data.features.marketdata.local.CoinsMarketDataDao
import com.cryptodeets.data.features.settings.local.SettingsDao
import com.cryptodeets.data.features.topcoins.local.TopCoinsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton



@Module
@InstallIn(SingletonComponent::class)
object LocalModule {

    @Provides
    @Singleton
    fun providePreferencesDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    @Provides
    @Singleton
    fun provideTopCoinsDao(
        coinsDatabase: CoinsDatabase
    ): TopCoinsDao = coinsDatabase.topCoinsDao()


}