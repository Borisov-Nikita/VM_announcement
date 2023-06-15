package nik.borisov.vmannouncement.data

import nik.borisov.vmannouncement.data.database.models.AnnouncementItemDbModel
import nik.borisov.vmannouncement.data.database.models.AnnouncementsReportDbModel
import nik.borisov.vmannouncement.data.database.models.TelegramBotDbModel
import nik.borisov.vmannouncement.data.network.models.AnnouncementDto
import nik.borisov.vmannouncement.data.network.models.LineDto
import nik.borisov.vmannouncement.data.network.models.PathDto
import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem
import nik.borisov.vmannouncement.domain.entities.TelegramBot
import nik.borisov.vmannouncement.utils.TimeConverter
import javax.inject.Inject

class Mapper @Inject constructor() : TimeConverter {

    fun mapAnnouncementDtoToEntity(
        dto: AnnouncementDto
    ): AnnouncementItem {
        val sport = getSportField(dto.path)
        val league = getLeagueField(dto.path)
        val time = dto.time
        val teamNames = getTeamNamesField(dto.path)
        return AnnouncementItem(
            sport = sport,
            league = league,
            time = time,
            firstTeam = teamNames.split("vs")[0],
            secondTeam = teamNames.split("vs")[1],
            announcementText = getAnnouncementText(sport, league, time, teamNames)
        )
    }

    fun mapLineDtoToString(dto: LineDto): String {
        return getAnnouncementText(
            dto.sport,
            dto.league,
            dto.time * 1000,
            "${dto.firstTeamName} vs ${dto.secondTeamName}"
        )
    }

    fun mapAnnouncementsReportDbModelToEntity(dbModel: AnnouncementsReportDbModel): AnnouncementsReportItem {
        return AnnouncementsReportItem(
            info = dbModel.info,
            id = dbModel.id
        )
    }

    fun mapEntityToAnnouncementsReportDbModel(report: AnnouncementsReportItem): AnnouncementsReportDbModel {
        return AnnouncementsReportDbModel(
            info = report.info,
            id = report.id
        )
    }

    fun mapAnnouncementDbModelToEntity(dbModel: AnnouncementItemDbModel): AnnouncementItem {
        return AnnouncementItem(
            sport = dbModel.sport,
            league = dbModel.league,
            time = dbModel.time,
            firstTeam = dbModel.firstTeam,
            secondTeam = dbModel.secondTeam,
            announcementText = dbModel.announcementText,
            id = dbModel.id,
            reportId = dbModel.announcementsReportId
        )
    }

    fun mapEntityToAnnouncementDbModel(announcement: AnnouncementItem): AnnouncementItemDbModel {
        return AnnouncementItemDbModel(
            sport = announcement.sport,
            league = announcement.league,
            time = announcement.time,
            firstTeam = announcement.firstTeam,
            secondTeam = announcement.secondTeam,
            announcementText = announcement.announcementText,
            id = announcement.id,
            announcementsReportId = announcement.reportId
        )
    }

    fun mapTelegramBotDbModelToEntity(dbModel: TelegramBotDbModel?): TelegramBot? {
        return if (dbModel == null) {
            null
        } else {
            TelegramBot(
                token = dbModel.token,
                chatId = dbModel.chatId
            )
        }
    }

    fun mapEntityToTelegramBotDbModel(bot: TelegramBot): TelegramBotDbModel {
        return TelegramBotDbModel(
            token = bot.token,
            chatId = bot.chatId
        )
    }

    private fun getSportField(path: List<PathDto>): String {
        return path[0].nodeName
    }

    private fun getLeagueField(path: List<PathDto>): String {
        val subPath = path.subList(1, path.size - 1).map { it.nodeName }
        return subPath.joinToString(separator = ". ", postfix = "")
    }

    private fun getTeamNamesField(path: List<PathDto>): String {
        return path[path.size - 1].nodeName
    }

    private fun getAnnouncementText(
        sport: String,
        league: String,
        time: Long,
        teamNames: String
    ): String {
        return buildString {
            append(
                convertTimeDateFromMillisToString(time, "dd MMM HH:mm"),
                "\n",
                sport,
                ". ",
                teamNames,
                "\n",
                league
            )
        }
    }
}