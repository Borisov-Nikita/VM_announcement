package nik.borisov.vmannouncement.domain.repositories

import androidx.lifecycle.LiveData
import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem
import nik.borisov.vmannouncement.domain.entities.MessageItem
import nik.borisov.vmannouncement.domain.entities.TelegramBot
import nik.borisov.vmannouncement.utils.DataResult

interface Repository {

    suspend fun downloadAnnouncements(date: Long): DataResult<List<AnnouncementItem>>

    suspend fun downloadLine(firstTeam: String, secondTeam: String, time: Long): DataResult<String>

    fun getAnnouncementsReport(): LiveData<List<AnnouncementsReportItem>>

    suspend fun addAnnouncementsReport(report: AnnouncementsReportItem): Long

    suspend fun deleteAnnouncementReport(reportId: Long)

    fun getAnnouncements(reportId: Long): LiveData<List<AnnouncementItem>>

    suspend fun addAnnouncements(announcementList: List<AnnouncementItem>)

    suspend fun deleteAnnouncement(announcementId: Long)

    suspend fun sendTelegramMessage(message: MessageItem): DataResult<Unit>

    suspend fun getTelegramBot(): TelegramBot?

    suspend fun addTelegramBot(bot: TelegramBot)
}