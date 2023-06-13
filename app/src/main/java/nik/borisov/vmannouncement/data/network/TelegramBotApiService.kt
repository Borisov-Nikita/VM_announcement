package nik.borisov.vmannouncement.data.network

import nik.borisov.vmannouncement.data.network.models.TelegramResponse
import retrofit2.Response
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface TelegramBotApiService {

    @POST("/bot{bot_token}/sendMessage")
    suspend fun sendMessage(
        @Path("bot_token") botToken: String,
        @Query("chat_id") chatId: String,
        @Query("text") text: String
    ): Response<TelegramResponse>
}