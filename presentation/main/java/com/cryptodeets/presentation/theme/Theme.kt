package com.cryptodeets.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val StocksDarkColorScheme = darkColorScheme(
    background = GreyBackground,
    onBackground = PrimaryTextWhite,
    surface = GreyBackground,
    surfaceVariant = SelectedCoinPurple,
    onSurfaceVariant = PrimaryTextWhite,
    primary = GreyBackground,
    secondary = GreyBackground,
    tertiary = SelectedCardPurple,
    onSurface = PrimaryTextWhite,
    onPrimaryContainer = PrimaryTextWhite,
    primaryContainer = SelectedCardPurple,
    onSecondaryContainer = SecondaryText,
    onPrimary = SecondaryText
)

@Composable
fun CryptodeetsTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = StocksDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}