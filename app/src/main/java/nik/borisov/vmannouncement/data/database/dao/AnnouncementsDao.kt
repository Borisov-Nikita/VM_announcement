package nik.borisov.vmannouncement.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import nik.borisov.vmannouncement.data.database.models.AnnouncementItemDbModel
import nik.borisov.vmannouncement.data.database.models.AnnouncementsReportDbModel
import nik.borisov.vmannouncement.data.database.models.TelegramBotDbModel

@Dao
interface AnnouncementsDao {

    @Query("SELECT * FROM announcements_report")
    fun getAnnouncementsReport(): LiveData<List<AnnouncementsReportDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAnnouncementsReport(report: AnnouncementsReportDbModel): Long

    @Query("DELETE FROM announcements_report WHERE id =:reportId")
    suspend fun deleteAnnouncementReport(reportId: Long)

    @Query("SELECT * FROM announcements WHERE id_announcements_report =:reportId")
    fun getAnnouncements(reportId: Long): LiveData<List<AnnouncementItemDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAnnouncementItem(announcement: AnnouncementItemDbModel)

    @Query("DELETE FROM announcements WHERE id =:announcementId")
    suspend fun deleteAnnouncement(announcementId: Long)

    @Query("SELECT * FROM telegram_bot WHERE id =:botId")
    suspend fun getTelegramBot(botId: Int): TelegramBotDbModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTelegramBot(bot: TelegramBotDbModel)
}