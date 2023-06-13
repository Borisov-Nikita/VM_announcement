package nik.borisov.vmannouncement.data

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import nik.borisov.vmannouncement.data.database.AppDatabase
import nik.borisov.vmannouncement.data.network.ApiFactory
import nik.borisov.vmannouncement.data.network.models.LineDto
import nik.borisov.vmannouncement.data.network.models.LineRequest
import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem
import nik.borisov.vmannouncement.domain.entities.MessageItem
import nik.borisov.vmannouncement.domain.entities.TelegramBot
import nik.borisov.vmannouncement.domain.repositories.Repository
import nik.borisov.vmannouncement.utils.DataResult
import nik.borisov.vmannouncement.utils.NetworkResponse

class RepositoryImpl(application: Application) : Repository, NetworkResponse() {

    private val marathonBetApiService = ApiFactory.marathonBetApiService
    private val oneXStavkaApiService = ApiFactory.oneXStavkaApiService
    private val telegramBotApiService = ApiFactory.telegramBotApiService

    private val announcementsDao = AppDatabase.getInstance(application).getAnnouncementDao()

    private val mapper = Mapper()


    override suspend fun downloadAnnouncements(date: Long): DataResult<List<AnnouncementItem>> {
        val announcementNetworkResult = safeNetworkCall {
            marathonBetApiService.loadAnnouncement(
                LineRequest(date)
            )
        }
        return if (announcementNetworkResult is DataResult.Success) {
            DataResult.Success(
                announcementNetworkResult.data?.announcements?.filter {
                    it.href == null && it.path[0].nodeName == "Football"
                }?.map {
                    mapper.mapAnnouncementDtoToEntity(it)
                }?.sortedWith(compareBy({ it.time }, { it.league }))
            )
        } else {
            DataResult.Error(message = announcementNetworkResult.message)
        }
    }

    override suspend fun downloadLine(
        firstTeam: String,
        secondTeam: String,
        time: Long
    ): DataResult<String> {
        val lineResult = mergeLineNetworkResult(
            time,
            checkLineWithTeam(firstTeam),
            checkLineWithTeam(secondTeam)
        )
        return if (lineResult is DataResult.Success) {
            DataResult.Success(
                lineResult.data?.filter {
                    it.sport == "Football" && it.time == time / 1000
                }?.sortedBy {
                    it.time
                }?.joinToString(separator = "\n\n") {
                    mapper.mapLineDtoToString(it)
                }
            )
        } else {
            DataResult.Error(message = lineResult.message)
        }
    }

    override fun getAnnouncementsReport(): LiveData<List<AnnouncementsReportItem>> {
        return announcementsDao.getAnnouncementsReport().map { dbModelList ->
            dbModelList.map { dbModelItem ->
                mapper.mapAnnouncementsReportDbModelToEntity(dbModelItem)
            }.asReversed()
        }
    }

    override suspend fun addAnnouncementsReport(report: AnnouncementsReportItem): Long {
        return announcementsDao.addAnnouncementsReport(
            mapper.mapEntityToAnnouncementsReportDbModel(
                report
            )
        )
    }

    override suspend fun deleteAnnouncementReport(reportId: Long) {
        announcementsDao.deleteAnnouncementReport(reportId)
    }

    override fun getAnnouncements(reportId: Long): LiveData<List<AnnouncementItem>> {
        return announcementsDao.getAnnouncements(reportId).map { dbModelList ->
            dbModelList.map { dbModelItem ->
                mapper.mapAnnouncementDbModelToEntity(dbModelItem)
            }
        }
    }

    override suspend fun addAnnouncements(announcementList: List<AnnouncementItem>) {
        for (announcement in announcementList) {
            announcementsDao.addAnnouncementItem(
                mapper.mapEntityToAnnouncementDbModel(announcement)
            )
        }
    }

    override suspend fun deleteAnnouncement(announcementId: Long) {
        announcementsDao.deleteAnnouncement(announcementId)
    }

    override suspend fun sendTelegramMessage(message: MessageItem): DataResult<Unit> {
        val telegramNetworkResult = safeNetworkCall {
            telegramBotApiService.sendMessage(
                message.bot.token,
                message.bot.chatId,
                message.messageText
            )
        }
        return if (telegramNetworkResult is DataResult.Success) {
            val status = telegramNetworkResult.data?.status
            if (status != null && status) {
                DataResult.Success(null)
            } else {
                DataResult.Error(message = telegramNetworkResult.data?.errorMessage)
            }
        } else {
            DataResult.Error(message = telegramNetworkResult.message)
        }
    }

    override suspend fun getTelegramBot(): TelegramBot? {
        return mapper.mapTelegramBotDbModelToEntity(announcementsDao.getTelegramBot(1))
    }

    override suspend fun addTelegramBot(bot: TelegramBot) {
        announcementsDao.addTelegramBot(mapper.mapEntityToTelegramBotDbModel(bot))
    }


    private suspend fun checkLineWithTeam(teamName: String): DataResult<List<LineDto>> {
        val lineNetworkResult = safeNetworkCall {
            oneXStavkaApiService.checkLine(teamName)
        }
        return if (lineNetworkResult is DataResult.Success) {
            DataResult.Success(
                lineNetworkResult.data?.value
            )
        } else {
            DataResult.Error(message = lineNetworkResult.message)
        }
    }

    private fun mergeLineNetworkResult(
        time: Long,
        vararg results: DataResult<List<LineDto>>
    ): DataResult<List<LineDto>> {
        val resultSet = mutableSetOf<LineDto>()
        val errorMessage = mutableListOf<String>()
        for (result in results) {
            if (result is DataResult.Success) {
                try {
                    resultSet.addAll(result.data?.filter { it.time == time / 1000 } ?: emptyList())
                } catch (e: Exception) {
                    e.message?.let { errorMessage.add(it) }
                }
            } else {
                errorMessage.add(result.message ?: "")
            }
        }
        return if (errorMessage.isNotEmpty()) {
            DataResult.Error(message = errorMessage.joinToString(", "))
        } else {
            DataResult.Success(
                resultSet.toList()
            )
        }
    }
}