/**
 * Dagger module providing dependencies related to API services.
 */
@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    // Number of minutes to wait when the maximum requests limit is reached
    private const val MINUTES_TO_WAIT_WHEN_REACHED_MAX_REQUESTS = 1

    // Variable to track the time when the maximum requests limit is reached
    private var timeWhenMaxRequestsLimitIsReached: LocalDateTime? = null

    /**
     * Provides the CoinGecko API service.
     */
    @Provides
    @Singleton
    fun provideCoinGeckoApiService(
        @ApplicationContext context: Context
    ): CoinGeckoApi {
        val cacheSize = 10 * 1024 * 1024L 
        val cache = Cache(context.cacheDir, cacheSize)
        val okHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor { chain ->

                Timber.d("NetworkInterceptor: ${chain.request()}")

                timeWhenMaxRequestsLimitIsReached?.let {
                    val minutesFromLimitReached = it.minutesBetween(LocalDateTime.now())

                    if (minutesFromLimitReached >= MINUTES_TO_WAIT_WHEN_REACHED_MAX_REQUESTS) {
                        resetLimitReachedState()
                    } else {
                        throw TemporarilyUnavailableNetworkServiceException(
                            serviceName = "CoinGecko"
                        )
                    }
                }

                val response = chain.proceed(chain.request())

                Timber.d("${response.headers()}")
                
                when(response.code()) {
                    429 -> {
                        setMaxRequestsLimitReached()
                        throw TemporarilyUnavailableNetworkServiceException(
                            serviceName = "CoinGecko"
                        )
                    }
                    else -> response
                }
                
            }
            .cache(cache)
            .build()

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(CoinGeckoApi.API_URL)
            .client(okHttpClient)
            .build()
            .create(CoinGeckoApi::class.java)
    }

    /**
     * Resets the state when the maximum requests limit is reached.
     */
    private fun resetLimitReachedState() {
        timeWhenMaxRequestsLimitIsReached = null
    }

    /**
     * Sets the state when the maximum requests limit is reached.
     */
    private fun setMaxRequestsLimitReached() {
        timeWhenMaxRequestsLimitIsReached = LocalDateTime.now()
    }

}
