package com.cryptodeets.presentation.commoncomposables

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.cryptodeets.presentation.R
import com.cryptodeets.presentation.theme.StocksDarkPrimaryText

@Composable
fun SectionTitle(
    modifier: Modifier = Modifier,
    title: String
) {
    Text(
        text = title,
        modifier = modifier.semantics { heading() },
        textAlign = TextAlign.Start,
        fontWeight = FontWeight.Bold,
        color = StocksDarkPrimaryText,
        style = MaterialTheme.typography.titleLarge,
        fontFamily = FontFamily(Font(R.font.sixty_font2)),
        fontSize = 14.sp

        )
}