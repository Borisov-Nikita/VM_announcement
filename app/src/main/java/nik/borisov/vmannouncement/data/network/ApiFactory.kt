package nik.borisov.vmannouncement.data.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiFactory {

    private const val BASE_URL_MARATHON_BET = "https://www.marathonbet.ru/"
    private const val BASE_URL_ONE_X_STAVKA = "https://1xstavka.ru/"
    private const val BASE_URL_TELEGRAM_BOT = "https://api.telegram.org/"

    private val retrofitMarathonBet = Retrofit.Builder()
        .baseUrl(BASE_URL_MARATHON_BET)
        .addConverterFactory(GsonConverterFactory.create())
        .client(createOkHttpClient())
        .build()

    private val retrofitOneXStavka = Retrofit.Builder()
        .baseUrl(BASE_URL_ONE_X_STAVKA)
        .addConverterFactory(GsonConverterFactory.create())
        .client(createOkHttpClient())
        .build()

    private val retrofitTelegramBot = Retrofit.Builder()
        .baseUrl(BASE_URL_TELEGRAM_BOT)
        .addConverterFactory(GsonConverterFactory.create())
        .client(createOkHttpClient())
        .build()

    private fun createHttpLoggingInterceptor() = HttpLoggingInterceptor()
        .apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    private fun createOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(createHttpLoggingInterceptor())
        .build()

    val marathonBetApiService = retrofitMarathonBet.create(MarathonBetApiService::class.java)
    val oneXStavkaApiService = retrofitOneXStavka.create(OneXStavkaApiService::class.java)
    val telegramBotApiService = retrofitTelegramBot.create(TelegramBotApiService::class.java)
}