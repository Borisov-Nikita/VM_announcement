package nik.borisov.vmannouncement.presentation

data class SearchAnnouncementSettings(

    val date: DateForAnnouncements,
    val timeFrom: Int,
    val timeTo: Int
)
