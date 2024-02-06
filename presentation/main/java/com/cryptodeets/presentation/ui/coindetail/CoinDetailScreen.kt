package com.cryptodeets.presentation.ui.coindetail

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import com.cryptodeets.presentation.R
import com.cryptodeets.presentation.commoncomposables.CoinIcon
import com.cryptodeets.presentation.commoncomposables.InfoTitle
import com.cryptodeets.presentation.commoncomposables.LoadingItem
import com.cryptodeets.presentation.models.*
import com.cryptodeets.presentation.theme.StocksDarkBackgroundTranslucent
import com.cryptodeets.presentation.theme.StocksDarkPrimaryText
import com.cryptodeets.presentation.theme.StocksDarkSecondaryText
import com.cryptodeets.presentation.theme.StocksDarkSelectedChip
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.pop

private val defaultHorizontalPadding = 16.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinDetailScreen(
    coinDetailMainUiData: CoinUiItem,
    navController: NavController<Screen>,
    viewModel: CoinDetailViewModel = hiltViewModel(
        defaultArguments = bundleOf(COIN_DETAIL_PARAMETER to coinDetailMainUiData)
    )
) {

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = { navController.pop() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_back_ios),
                            contentDescription = "Return to previous screen",
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                },
                actions = {


                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->


        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
        ) {

            item {
                Header(coinDetailMainUiData)
                Spacer(modifier = Modifier.size(16.dp))
            }

            item {
                Price(state = viewModel.state.coinMarketDataState, percentageChange = viewModel.state.coinMarketChartState)
            }
            item {

                Column(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .background(
                            color = StocksDarkBackgroundTranslucent,
                            shape = MaterialTheme.shapes.large
                        ),
                ) {

                    when (val state = viewModel.state.coinMarketDataState) {
                        is CoinMarketDataState.Success -> {
                            val list = state.data.marketDataList

                            list.forEachIndexed { index, pair ->
                                if (pair.second.isEmpty()) {
                                    InfoTitle(title = pair.first)
                                } else {
                                    SectionInfoItem(
                                        name = pair.first,
                                        value = pair.second,
                                        showDivider = index != list.lastIndex
                                    )
                                }
                            }
                        }
                        is CoinMarketDataState.Error -> {
                             Text(
                                text = "Error loading data.",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically)
                                    .padding(16.dp),
                                color = StocksDarkPrimaryText,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.bodyMedium
                            )


                            LaunchedEffect(key1 = snackbarHostState) {

                                val result = snackbarHostState.showSnackbar(
                                    message = state.message,
                                    actionLabel = "Retry",
                                    withDismissAction = true,
                                    duration = SnackbarDuration.Indefinite
                                )

                                if (result == SnackbarResult.ActionPerformed) {
                                    viewModel.onMarketDataErrorRetry()
                                }

                            }
                        }
                        else -> {
                            LoadingItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically)
                                    .padding(16.dp),
                                text = "Loading data..."
                            )
                        }
                    }

                }
            }
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeight(190.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {

                    when (val state = viewModel.state.coinMarketChartState) {
                        is CoinMarketChartState.Success -> {


                            Spacer(modifier = Modifier.size(16.dp))


                            LaunchedEffect(null) {
                                viewModel.showMarketChart()
                            }

                        }
                    
                    }
                }

                Spacer(modifier = Modifier.size(16.dp))


            }



        }

    }

}

@Composable
fun SectionInfoItem(
    name: String,
    value: String,
    showDivider: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(defaultHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
       Text(
            text = name,
            color = StocksDarkSecondaryText,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily(Font(R.font.sixty_font2)),
            fontSize = 9.sp
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = value,
            fontWeight = FontWeight.SemiBold,
            color = StocksDarkPrimaryText,
            style = MaterialTheme.typography.bodyMedium,
            fontFamily = FontFamily(Font(R.font.sixty_font2)),
            fontSize = 9.sp
        )
    }

    if (showDivider) {
        Divider(
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .height(4.dp),
            color = StocksDarkSelectedChip
        )
    }
}

@Composable
fun PriceText(
    modifier: Modifier,
    price: String?
) {
    Text(
        modifier = modifier.alpha(
            alpha = if (price == null) 0f else 1f
        ),
        textAlign = TextAlign.Start,
        text = price ?: "000000000",
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily(Font(R.font.sixty_font2)),
        fontSize = 14.sp,
        color = StocksDarkPrimaryText,
        maxLines = 1
    )
}


@Composable
fun Header(coinDetailMainUiData: CoinUiItem) {
    Row(
        modifier = Modifier
            .padding(horizontal = defaultHorizontalPadding)
            .fillMaxWidth()
            .wrapContentHeight()
            .background(MaterialTheme.colorScheme.background),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {

            CoinIcon(
                imageUrl = coinDetailMainUiData.imageUrl,
                size = 50.dp,
                shape = MaterialTheme.shapes.large,
                modifier = Modifier.size(50.dp)
            )
        }


        Spacer(modifier = Modifier.size(16.dp))

        Row(
            horizontalArrangement  = Arrangement.Center
        ) {
            Text(
                text = coinDetailMainUiData.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = StocksDarkPrimaryText,
                fontSize = 12.sp,
                fontFamily = FontFamily(Font(R.font.sixty_font2)),
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(Alignment.Center)
            )
        }

}


@Composable
fun Price(state: CoinMarketDataState, percentageChange: CoinMarketChartState) {
    val price = when (state) {
        is CoinMarketDataState.Success -> state.data.price
        else -> null
    }

    val percentage = when (percentageChange) {
        is CoinMarketChartState.Success -> percentageChange.data.priceChangePercentage
        else -> null
    }

    val percentageColor = when (percentageChange) {
        is CoinMarketChartState.Success -> percentageChange.data.trendColor
        else -> StocksDarkPrimaryText
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PriceText(
            modifier = Modifier.weight(1f).padding(start = 8.dp),
            price = price
        )

        Text(
            text = percentage ?: "",
            style = MaterialTheme.typography.titleMedium,
            color = percentageColor,
            modifier = Modifier
                .weight(0.5f)
                .padding(horizontal = defaultHorizontalPadding),
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            maxLines = 1,
            fontFamily = FontFamily(Font(R.font.sixty_font2)),
            fontSize = 11.sp
        )
    }
}
