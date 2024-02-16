package com.cryptodeets.presentation

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.cryptodeets.presentation.models.CoinsListUiState
import com.cryptodeets.presentation.models.Screen
import com.cryptodeets.presentation.theme.*
import com.cryptodeets.presentation.ui.coindetail.CoinDetailScreen
import com.cryptodeets.presentation.ui.coinslist.CoinsListScreen
import com.cryptodeets.presentation.ui.coinslist.CoinsListViewModel
import dagger.hilt.android.AndroidEntryPoint
import dev.olshevski.navigation.reimagined.*
import dev.olshevski.navigation.reimagined.hilt.hiltViewModel
@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val startDestinationViewModel: CoinsListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CryptodeetsTheme {

                val navController = rememberNavController<Screen>(
                    startDestination = Screen.CoinsList
                )

                NavBackHandler(navController)


                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,

                    ) {
                    Surface(modifier = Modifier.padding(it)) {
                        NavHost(
                            controller = navController,
                            //transitionSpec = mainNavHostTransitionSpec
                        ) { route ->
                            when (route) {
                                is Screen.CoinsList -> {
                                    CoinsListScreen(
                                        navController = navController,
                                        viewModel = startDestinationViewModel
                                    )
                                }

                                is Screen.CoinDetail -> {
                                    CoinDetailScreen(
                                        coinDetailMainUiData = route.coinDetailMainData,
                                        navController = navController
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    }


}
