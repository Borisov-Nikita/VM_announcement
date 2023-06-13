package nik.borisov.vmannouncement.data.network.models

import com.google.gson.annotations.SerializedName

data class AnnouncementResponse(

    @SerializedName("announces")
    val announcements: List<AnnouncementDto>
)
