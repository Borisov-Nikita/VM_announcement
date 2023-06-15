package nik.borisov.vmannouncement.data.network.models

import com.google.gson.annotations.SerializedName

data class DateRequest(

    @SerializedName("date")
    val date: Long
)
