package com.cryptodeets.presentation.ui.coinslist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptodeets.domain.features.topcoins.GetTopCoinsFlowUseCase
import com.cryptodeets.domain.features.topcoins.RefreshTopCoinsUseCase
import com.cryptodeets.domain.features.topcoins.models.TopCoinsData
import com.cryptodeets.presentation.mappers.UiMapper
import com.cryptodeets.presentation.models.CoinsListState
import com.cryptodeets.presentation.models.CoinsListUiState
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.haan.resultat.*
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CoinsListViewModel @Inject constructor(
    private val getTopCoinsFlowUseCase: GetTopCoinsFlowUseCase,
    private val refreshTopCoinsUseCase: RefreshTopCoinsUseCase,
    private val mapper: UiMapper,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    var state by mutableStateOf(
        CoinsListState(
            topCoinsList = persistentListOf(),
            lastUpdateDate = "",
            state = CoinsListUiState.Idle
        )
    )
    private set

    private var topCoinsFlowJob: Job? = null

    private val isTopCoinsFlowInterrupted get() = topCoinsFlowJob == null


    init {
        initTopCoinsFlowCollection()
    }

// Initializes the top coins flow collection by launching a coroutine job that observes the flow of top coins.
    private fun initTopCoinsFlowCollection() {

        topCoinsFlowJob = getTopCoinsFlowUseCase(Unit)
            .onEach {
                handleGetTopCoinsState(it)
            }.catch {
                handleGetTopCoinsState(Resultat.failure(it))

        
            }.launchIn(viewModelScope)
    }

// Handles the states emitted by the top coins flow.
    private suspend fun handleGetTopCoinsState(result: Resultat<TopCoinsData>) {
        result.onSuccess {
            val uiData = withContext(dispatcherProvider.default) {
                mapper.mapTopCoinUiData(it)
            }

            state = state.copy(
                topCoinsList = uiData.topCoins,
                lastUpdateDate = uiData.lastUpdate,
                state = CoinsListUiState.Idle
            )
        }.onFailure {
            state = state.copy(
                state = CoinsListUiState.Error(message = mapper.mapErrorToUiMessage(it))
            )
        }.onLoading {
            state = state.copy(
                state = CoinsListUiState.Refreshing(isAutomaticRefresh = true)
            )
        }
    }

    private fun handleRefreshTopCoinsState(result: Resultat<Unit>) {
        result.onSuccess {
            state = state.copy(state = CoinsListUiState.Idle)
        }.onFailure {
            state = state.copy(
                state = CoinsListUiState.Error(message = mapper.mapErrorToUiMessage(it))
            )
        }.onLoading {
            state = state.copy(
                state = CoinsListUiState.Refreshing(isAutomaticRefresh = false)
            )
        }
    }
}