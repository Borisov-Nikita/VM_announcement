package nik.borisov.vmannouncement.data.network.models

import com.google.gson.annotations.SerializedName
import java.util.*

data class AnnouncementDto(

    @SerializedName("time")
    val time: Long,
    @SerializedName("path")
    val path: List<PathDto>,
    @SerializedName("href")
    val href: String?
)
