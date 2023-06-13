package nik.borisov.vmannouncement.data.network.models

import com.google.gson.annotations.SerializedName

data class LineResponse(

    @SerializedName("Value")
    val value: List<LineDto>?
)
