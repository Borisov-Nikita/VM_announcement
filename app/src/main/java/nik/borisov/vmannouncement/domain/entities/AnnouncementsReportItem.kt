package nik.borisov.vmannouncement.domain.entities

data class AnnouncementsReportItem(

    val info: String,
    val id: Long = UNDEFINED_ID
) {

    companion object {

        private const val UNDEFINED_ID: Long = 0
    }
}
