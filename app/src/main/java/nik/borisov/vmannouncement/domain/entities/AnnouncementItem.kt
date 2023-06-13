package nik.borisov.vmannouncement.domain.entities

data class AnnouncementItem(

    val sport: String,
    val league: String,
    val time: Long,
    val firstTeam: String,
    val secondTeam: String,
    val announcementText: String,
    val id: Long = UNDEFINED_ID,
    var reportId: Long = UNDEFINED_ID
) {

    companion object {

        private const val UNDEFINED_ID: Long = 0
    }
}