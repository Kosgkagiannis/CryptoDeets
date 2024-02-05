package com.cryptodeets.presentation.ui.coinslist

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptodeets.presentation.R
import com.cryptodeets.presentation.commoncomposables.CoinWithMarketDataItem
import com.cryptodeets.presentation.commoncomposables.SectionTitle
import com.cryptodeets.presentation.models.COINS_LIST_SCREEN_KEY
import com.cryptodeets.presentation.models.CoinUiItem
import com.cryptodeets.presentation.models.CoinsListUiState
import com.cryptodeets.presentation.models.Screen
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import dev.olshevski.navigation.reimagined.NavController
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
import dev.olshevski.navigation.reimagined.navigate


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CoinsListScreen(
    navController: NavController<Screen>,
    viewModel: CoinsListViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    val coinsListState = rememberLazyListState()

    val swipeRefreshState = remember {
        derivedStateOf {
            viewModel.state.state == CoinsListUiState.Refreshing(isAutomaticRefresh = false)
        }
    }



    val snackbarHostState = remember { SnackbarHostState() }

    val goToCoinDetail: (CoinUiItem) -> Unit = {
        navController.navigate(
            Screen.CoinDetail(
                coinDetailMainData = it
            )
        )
    }

    // Filter the list of coins based on the search query
    val filteredCoins = viewModel.state.topCoinsList.filter { coin ->
        coin.name.contains(searchQuery, ignoreCase = true) ||
                coin.symbol.contains(searchQuery, ignoreCase = true)
    }

    val placeholderTextStyle = TextStyle(
        fontFamily = FontFamily(Font(R.font.sixty_font2)),
        fontSize = 9.sp,
    )

    Scaffold(
        modifier = Modifier,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        modifier = Modifier.fillMaxWidth() .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                        value = searchQuery,
                        onValueChange = { newValue ->
                            searchQuery = newValue
                        },
                        placeholder = {
                            Text(
                                text = "Search cryptocurrencies",
                                style = placeholderTextStyle
                            )
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium,
                        shape = MaterialTheme.shapes.small,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon",
                                modifier = Modifier.padding(12.dp)
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { searchQuery = "" }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear Search",
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    )
                }
            )
        }
    ) { innerPadding ->
        SwipeRefresh(
            state = rememberSwipeRefreshState(isRefreshing = swipeRefreshState.value),
            onRefresh = {  },
            modifier = Modifier.fillMaxSize(),
            indicatorPadding = innerPadding,
        ) {
            LazyColumn(
                state = coinsListState,
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SectionTitle(
                        modifier = Modifier.padding(start = 32.dp, end = 32.dp, top = 32.dp, bottom = 16.dp),
                        title = "Leading Cryptocurrencies"
                    )
                }

                val chunkedList = filteredCoins.chunked(3)

                chunkedList.forEach { rowItems ->
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Display items in the row
                            rowItems.forEach { item ->
                                Box(
                                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                                ) {
                                    CoinWithMarketDataItem(
                                        modifier = Modifier.fillMaxWidth(),
                                        item = { item },
                                        sharedElementScreenKey = { COINS_LIST_SCREEN_KEY },
                                        onCoinItemClick = {
                                            goToCoinDetail(
                                                with(item) {
                                                    CoinUiItem(
                                                        id = id,
                                                        name = name,
                                                        symbol = symbol,
                                                        imageUrl = imageUrl,
                                                        marketCapRank = marketCapRank,
                                                    )
                                                }
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.size(32.dp))
                }
            }
        }
    }
}





