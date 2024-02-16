package com.cryptodeets.presentation.ui.coindetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptodeets.domain.features.marketchart.GetMarketChartDataUseCase
import com.cryptodeets.domain.features.marketdata.GetCoinMarketDataFlowUseCase
import com.cryptodeets.domain.features.marketdata.models.CoinMarketDataInputParams
import com.cryptodeets.domain.features.settings.models.GlobalSettingsConfiguration
import com.cryptodeets.domain.models.CoinMarketData
import com.cryptodeets.presentation.mappers.UiMapper
import com.cryptodeets.presentation.models.*
import com.github.davidepanidev.kotlinextensions.utils.dispatchers.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import fr.haan.resultat.Resultat
import fr.haan.resultat.onFailure
import fr.haan.resultat.onLoading
import fr.haan.resultat.onSuccess
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CoinDetailViewModel @Inject constructor(
    private val getCoinMarketDataFlowUseCase: GetCoinMarketDataFlowUseCase,
    private val getMarketChartDataUseCase: GetMarketChartDataUseCase,
    private val mapper: UiMapper,
    private val savedStateHandle: SavedStateHandle,
    private val dispatcherProvider: DispatcherProvider
) : ViewModel() {

    private val coinDetailMainUiData: CoinUiItem
        get() = savedStateHandle[COIN_DETAIL_PARAMETER]
            ?: throw RuntimeException("CoinDetailViewModel must be provided with $COIN_DETAIL_PARAMETER parameter.")

    var state by mutableStateOf(
        CoinDetailState(
            coinMarketDataState = CoinMarketDataState.Success(
                data = CoinMarketUiData(
                    price = "",
                    marketDataList = persistentListOf()
                )
            ),
            coinMarketChartState = CoinMarketChartState.Loading,
            isMarketChartVisible = false,
        )
    )
    private set

    private var getMatketChartJob: Job? = null


    init {
        getCoinMarketData()
        getMarketChartData()
    }

    private fun getCoinMarketData() {
        getCoinMarketDataFlowUseCase(
            inputParams = CoinMarketDataInputParams(
                coinId = coinDetailMainUiData.id
            )
        ).onEach {
            handleGetCoinMarketDataState(it)
        }.catch {
            handleGetCoinMarketDataState(Resultat.failure(it))
        }.launchIn(viewModelScope)
    }

    private fun handleGetCoinMarketDataState(result: Resultat<CoinMarketData>) {
        var newState: CoinMarketDataState = state.coinMarketDataState

        result.onSuccess {
            val uiData = mapper.mapCoinMarketUiData(it)

            newState = CoinMarketDataState.Success(uiData)
        }.onFailure {
            newState = CoinMarketDataState.Error(message = mapper.mapErrorToUiMessage(it))
        }.onLoading {
            newState = CoinMarketDataState.Loading
        }

        state = state.copy(
            coinMarketDataState = newState
        )
    }


    private fun getMarketChartData() {

        val oldJob = getMatketChartJob

        getMatketChartJob = viewModelScope.launch {
            oldJob?.cancelAndJoin()

            val loadingJob = async {
                delay(250)
                state = state.copy(
                    coinMarketChartState = CoinMarketChartState.Loading
                )
            }

            val getDataJob = async {
                getMarketChartDataUseCase(
                    coinId = coinDetailMainUiData.id
                )
            }

            val result = getDataJob.await()
            loadingJob.cancelAndJoin()

            var newState = state.coinMarketChartState

            result.onSuccess {
                newState = CoinMarketChartState.Success(
                    data = withContext(dispatcherProvider.default) {
                        mapper.mapMarketChartUiData(it)
                    }
                )
            }.onFailure {
                newState = CoinMarketChartState.Error(message = mapper.mapErrorToUiMessage(it))
            }

            delay(200L)
            state = state.copy(
                coinMarketChartState = newState
            )

            getMatketChartJob = null
        }
    }

    fun showMarketChart() {
        if (!state.isMarketChartVisible) {
            state = state.copy(
                isMarketChartVisible = true
            )
        }
    }

    fun onMarketChartErrorRetry() {
        onErrorRetry()
    }

    fun onMarketDataErrorRetry() {
        onErrorRetry()
    }

    private fun onErrorRetry() {
        if (state.coinMarketChartState is CoinMarketChartState.Error) {
            getMarketChartData()
        }

        if (state.coinMarketDataState is CoinMarketDataState.Error) {
            getCoinMarketData()
        }
    }

    }

