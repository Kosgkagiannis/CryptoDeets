package com.cryptodeets.domain.models

enum class Ordering {
    MarketCapAsc,
    MarketCapDesc,
    PriceAsc,
    PriceDesc,
    PriceChangeAsc,
    PriceChangeDesc,
    NameAsc,
    NameDesc,
    VolumeAsc,
    VolumeDesc,
    DividendYieldAsc,
    DividendYieldDesc
};


enum class Currency {
    USD,
    EUR,
    GBP,
    JPY,
    AUD,
    CAD,
    CHF,
    CNY,
    BTC,
    ETH
};

enum class TimeRange {
    Day,
    Week,
    Month,
    SixMonths,
    Year,
    Max
}