package com.cryptodeets.presentation.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface Screen : Parcelable {

    @Parcelize
    object CoinsList : Screen

    @Parcelize
    data class CoinDetail(val coinDetailMainData: CoinUiItem) : Screen

}
