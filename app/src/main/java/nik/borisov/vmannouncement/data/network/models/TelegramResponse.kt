package nik.borisov.vmannouncement.data.network.models

import com.google.gson.annotations.SerializedName

data class TelegramResponse(

    @SerializedName("ok")
    val status: Boolean,
    @SerializedName("description")
    val errorMessage: String?
)