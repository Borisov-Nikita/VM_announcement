package nik.borisov.vmannouncement.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import nik.borisov.vmannouncement.data.database.dao.AnnouncementsDao
import nik.borisov.vmannouncement.data.network.models.DateRequest
import nik.borisov.vmannouncement.data.network.models.LineDto
import nik.borisov.vmannouncement.data.network.services.MarathonBetApiService
import nik.borisov.vmannouncement.data.network.services.OneXStavkaApiService
import nik.borisov.vmannouncement.data.network.services.TelegramBotApiService
import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem
import nik.borisov.vmannouncement.domain.entities.MessageItem
import nik.borisov.vmannouncement.domain.entities.TelegramBot
import nik.borisov.vmannouncement.domain.repositories.Repository
import nik.borisov.vmannouncement.utils.DataResult
import nik.borisov.vmannouncement.utils.NetworkResponse
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val marathonBetApiService: MarathonBetApiService,
    private val oneXStavkaApiService: OneXStavkaApiService,
    private val telegramBotApiService: TelegramBotApiService,
    private val announcementsDao: AnnouncementsDao,
    private val mapper: Mapper
) : Repository, NetworkResponse() {

    override suspend fun downloadAnnouncements(date: Long): DataResult<List<AnnouncementItem>> {
        val announcementNetworkResult = safeNetworkCall {
            marathonBetApiService.loadAnnouncement(
                DateRequest(date)
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
        val lineResult = checkLineWithTeam(firstTeam, secondTeam)
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

    override suspend fun deleteAnnouncementsReport(reportId: Long) {
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
            announcementsDao.addAnnouncement(
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

    private suspend fun checkLineWithTeam(vararg teamNames: String): DataResult<List<LineDto>> {
        val resultSet = mutableSetOf<LineDto>()
        val errorList = mutableListOf<String>()
        for (teamName in teamNames) {
            for (word in teamName.split(" ")) {
                val lineNetworkResult = safeNetworkCall {
                    oneXStavkaApiService.checkLine(word)
                }
                if (lineNetworkResult is DataResult.Success) {
                    try {
                        resultSet.addAll(lineNetworkResult.data?.value ?: emptyList())
                    } catch (e: Exception) {
                        e.message?.let { errorList.add(it) }
                    }
                } else {
                    errorList.add(lineNetworkResult.message ?: "")
                }
            }
        }
        return if (resultSet.isEmpty() && errorList.isNotEmpty()) {
            DataResult.Error(message = errorList.joinToString(", "))
        } else {
            DataResult.Success(
                resultSet.toList()
            )
        }
    }
}