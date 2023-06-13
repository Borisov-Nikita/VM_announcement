package nik.borisov.vmannouncement.data.network.models

import com.google.gson.annotations.SerializedName

data class LineDto(

    @SerializedName("S")
    val time: Long,
    @SerializedName("LE")
    val league: String,
    @SerializedName("SE")
    val sport: String,
    @SerializedName("O1E")
    val firstTeamName: String,
    @SerializedName("O2E")
    val secondTeamName: String
)
