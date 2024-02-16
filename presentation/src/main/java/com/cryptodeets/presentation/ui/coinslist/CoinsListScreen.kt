package com.cryptodeets.presentation.commoncomposables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptodeets.presentation.R
import com.cryptodeets.presentation.models.BaseCoinWithMarketDataUiItem
import com.cryptodeets.presentation.models.CoinWithMarketDataUiItem
import com.cryptodeets.presentation.models.DataPoint
import com.cryptodeets.presentation.theme.CryptodeetsTheme
import com.cryptodeets.presentation.theme.PositiveTrend
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.format.TextStyle

@Composable
@Suppress("UNUSED_PARAMETER")
fun CoinWithMarketDataItem(
    modifier: Modifier = Modifier,
    item: () -> BaseCoinWithMarketDataUiItem,
    sharedElementScreenKey: () -> String,
    onCoinItemClick: () -> Unit,
) {
    Timber.d("CoinItem recomposition")

    val coroutineScope = rememberCoroutineScope()

    val setupSharedElement = rememberSaveable {
        mutableStateOf(false)
    }

    Box(
        modifier = Modifier
            .size(128.dp)
            .clickable {
                if (setupSharedElement.value) {
                    onCoinItemClick()
                } else {
                    setupSharedElement.value = true

                    coroutineScope.launch {
                        delay(50L)
                        onCoinItemClick()
                    }
                }
            }
            .background(
                color = Color(0x996A00FF),
                shape = CircleShape
            ).border(1.dp, Color.White, CircleShape)

    ) {

        Column(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Timber.d("CoinItem card recomposition ${item().symbol} $sharedElementScreenKey")

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon
                CoinIcon(imageUrl = item().imageUrl)

                Spacer(modifier = Modifier.height(8.dp))

                // Name
                Text(
                    text = item().name,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                    fontFamily = FontFamily(Font(R.font.sixty_font2)),
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Price and Percentage
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item().price,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily(Font(R.font.sixty_font2)),
                        fontSize = 10.sp,
                        maxLines = 1
                    )
                }

                    }
                }
            }
        }

