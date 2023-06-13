package nik.borisov.vmannouncement.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nik.borisov.vmannouncement.data.RepositoryImpl
import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem
import nik.borisov.vmannouncement.domain.entities.MessageItem
import nik.borisov.vmannouncement.domain.entities.TelegramBot
import nik.borisov.vmannouncement.domain.usecases.*
import nik.borisov.vmannouncement.utils.DataResult
import nik.borisov.vmannouncement.utils.TelegramBotHelper
import java.text.SimpleDateFormat
import java.time.*
import java.util.*

class SearchAnnouncementsViewModel(application: Application) : AndroidViewModel(application), TelegramBotHelper {

    private val repository = RepositoryImpl(application)
    private val downloadAnnouncementsUseCase = DownloadAnnouncementsUseCase(repository)
    private val downloadLineUseCase = DownloadLineUseCase(repository)
    private val addAnnouncementReportUseCase = AddAnnouncementsReportUseCase(repository)
    private val addAnnouncementsUseCase = AddAnnouncementsUseCase(repository)
    private val sendTelegramMessageUseCase =
        SendTelegramMessageUseCase(repository)
    private val getTelegramBotUseCase = GetTelegramBotUseCase(repository)

    private val _announcements = MutableLiveData<List<AnnouncementItem>>()
    val announcements: LiveData<List<AnnouncementItem>>
        get() = _announcements

    private val _line = MutableLiveData<DataResult<String>>()
    val line: LiveData<DataResult<String>>
        get() = _line

    private val _telegramBotError = MutableLiveData<String>()
    val telegramBotError: LiveData<String>
        get() = _telegramBotError

    private val announcementList = mutableListOf<AnnouncementItem>()

    private var searchAnnouncementSettings =
        SearchAnnouncementSettings(DateForAnnouncements.TODAY, 0, 23)

    private var telegramBot: TelegramBot? = null

    init {
        getAnnouncements()
        getTelegramBot()
    }

    fun getLine(firstTeam: String, secondTeam: String, time: Long) {
        viewModelScope.launch {
            val result = downloadLineUseCase.downloadLine(firstTeam, secondTeam, time)
            _line.value = result
        }
    }

    fun updateSearchAnnouncementSettings(settings: SearchAnnouncementSettings) {
        if (searchAnnouncementSettings.date != settings.date) {
            searchAnnouncementSettings = settings
            getAnnouncements()
        }
        if (
            searchAnnouncementSettings.timeFrom != settings.timeFrom
            || searchAnnouncementSettings.timeTo != settings.timeTo
        ) {
            searchAnnouncementSettings = settings
            refreshAnnouncements()
        }
    }

    fun deleteAnnouncementItem(announcementItem: AnnouncementItem) {
        announcementList.remove(announcementItem)
        refreshAnnouncements()
    }

    fun saveAnnouncements() {
        val announcementsValue = announcements.value
        if (announcementsValue != null) {
            viewModelScope.launch {
                val reportId = addAnnouncementReportUseCase.addAnnouncementsReport(
                    AnnouncementsReportItem(
                        info = parseReportInfo(
                            announcementsValue[0].time,
                            announcementsValue[announcementsValue.size - 1].time
                        )
                    )
                )
                addAnnouncementsUseCase.addAnnouncement(announcementsValue.map {
                    it.copy(reportId = reportId)
                })
            }
        }
    }

    fun sendAnnouncementsReport() {
        if (telegramBot != null) {
            val announcementsValue = announcements.value
            if (announcementsValue != null) {
                viewModelScope.launch {
                    sendTelegramMessageUseCase.sendTelegramMessage(
                        MessageItem(
                            bot = telegramBot ?: return@launch,
                            messageText = parseMessage(announcementsValue)
                        )
                    )
                    sendTelegramMessageUseCase.sendTelegramMessage(
                        MessageItem(
                            bot = telegramBot ?: return@launch,
                            messageText = parseShortAnnouncementsMessage(announcementsValue)
                        )
                    )
                }
            }
        } else {
            _telegramBotError.value = "Telegram bot is not configured."
        }
    }

    private fun getAnnouncements() {
        viewModelScope.launch {
            val result = downloadAnnouncementsUseCase.downloadAnnouncements(
                parseTimeFrom(0)
            )
            when (result) {
                is DataResult.Success -> {
                    announcementList.clear()
                    if (result.data != null) {
                        announcementList.addAll(result.data)
                    }
                    refreshAnnouncements()
                }
                is DataResult.Error -> {}
                is DataResult.Loading -> {}
            }
        }
    }

    private fun getTelegramBot() {
        viewModelScope.launch {
            telegramBot = getTelegramBotUseCase.getTelegramBot()
        }
    }

    private fun refreshAnnouncements() {
        val timeFromMilli = parseTimeFrom(searchAnnouncementSettings.timeFrom)
        val timeToMilli = parseTimeTo(searchAnnouncementSettings.timeTo)
        _announcements.value = announcementList.filter {
            it.time in timeFromMilli until timeToMilli
        }.toList()
    }

    private fun parseDate(date: DateForAnnouncements): LocalDate {
        return when (date) {
            DateForAnnouncements.TODAY -> {
                LocalDate.now(ZoneOffset.UTC)
            }
            DateForAnnouncements.TOMORROW -> {
                LocalDate.now(ZoneOffset.UTC).plusDays(1)
            }
        }
    }

    private fun parseTimeFrom(hour: Int): Long {
        return ZonedDateTime.of(
            parseDate(searchAnnouncementSettings.date),
            LocalTime.of(hour, 0),
            ZoneId.of("Europe/Moscow")
        ).toEpochSecond() * 1000
    }

    private fun parseTimeTo(hour: Int): Long {
        return ZonedDateTime.of(
            parseDate(searchAnnouncementSettings.date),
            LocalTime.of(hour, 59),
            ZoneId.of("Europe/Moscow")
        ).toEpochSecond() * 1000
    }

    private fun parseReportInfo(timeFrom: Long, timeTo: Long): String {
        val formatterDate = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)
        val formatterTime = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        formatterDate.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        formatterTime.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        return "${
            formatterDate.format(Date(timeFrom))
        }\n${
            formatterTime.format(Date(timeFrom))
        } - ${
            formatterTime.format(Date(timeTo))
        }"
    }
}