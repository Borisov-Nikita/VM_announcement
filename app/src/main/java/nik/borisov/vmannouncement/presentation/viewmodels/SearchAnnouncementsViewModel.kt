package nik.borisov.vmannouncement.presentation.viewmodels

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
import nik.borisov.vmannouncement.presentation.viewmodels.states.Announcements
import nik.borisov.vmannouncement.presentation.viewmodels.states.AnnouncementsState
import nik.borisov.vmannouncement.presentation.viewmodels.states.BotError
import nik.borisov.vmannouncement.presentation.viewmodels.states.Line
import nik.borisov.vmannouncement.utils.*
import java.time.*

class SearchAnnouncementsViewModel(application: Application) : AndroidViewModel(application),
    TelegramBotHelper, TimeConverter {

    private val repository = RepositoryImpl(application)
    private val downloadAnnouncementsUseCase = DownloadAnnouncementsUseCase(repository)
    private val downloadLineUseCase = DownloadLineUseCase(repository)
    private val addAnnouncementReportUseCase = AddAnnouncementsReportUseCase(repository)
    private val addAnnouncementsUseCase = AddAnnouncementsUseCase(repository)
    private val sendTelegramMessageUseCase =
        SendTelegramMessageUseCase(repository)
    private val getTelegramBotUseCase = GetTelegramBotUseCase(repository)

    private val _state = MutableLiveData<AnnouncementsState>()
    val state: LiveData<AnnouncementsState>
        get() = _state

    private val commonAnnouncementList = mutableListOf<AnnouncementItem>()
    private val visibleAnnouncementList = mutableListOf<AnnouncementItem>()

    private var searchAnnouncementSettings =
        SearchAnnouncementSettings(DateForAnnouncements.TODAY, 0, 23)

    private var telegramBot: TelegramBot? = null

    init {
        getAnnouncements()
        getTelegramBot()
    }

    fun getLine(firstTeam: String, secondTeam: String, time: Long) {
        viewModelScope.launch {
            _state.value = Line(downloadLineUseCase.downloadLine(firstTeam, secondTeam, time))
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

    fun deleteAnnouncement(announcement: AnnouncementItem) {
        commonAnnouncementList.remove(announcement)
        refreshAnnouncements()
    }

    fun saveAnnouncements() {
        if (visibleAnnouncementList.isNotEmpty()) {
            viewModelScope.launch {
                val reportId = addAnnouncementReportUseCase.addAnnouncementsReport(
                    AnnouncementsReportItem(
                        info = parseReportInfo(
                            visibleAnnouncementList[0].time,
                            visibleAnnouncementList[visibleAnnouncementList.size - 1].time
                        )
                    )
                )
                addAnnouncementsUseCase.addAnnouncement(visibleAnnouncementList.map {
                    it.copy(reportId = reportId)
                })
            }
        }
    }

    fun sendAnnouncementsReport() {
        if (telegramBot != null) {
            if (visibleAnnouncementList.isNotEmpty()) {
                viewModelScope.launch {
                    sendTelegramMessageUseCase.sendTelegramMessage(
                        MessageItem(
                            bot = telegramBot ?: return@launch,
                            messageText = parseMessage(visibleAnnouncementList)
                        )
                    )
                    sendTelegramMessageUseCase.sendTelegramMessage(
                        MessageItem(
                            bot = telegramBot ?: return@launch,
                            messageText = parseShortAnnouncementsMessage(visibleAnnouncementList)
                        )
                    )
                }
            }
        } else {
            _state.value = BotError
        }
    }

    private fun getAnnouncements() {
        viewModelScope.launch {
            val result = downloadAnnouncementsUseCase.downloadAnnouncements(
                parseTimeFrom(0)
            )
            when (result) {
                is DataResult.Success -> {
                    commonAnnouncementList.clear()
                    if (result.data != null) {
                        commonAnnouncementList.addAll(result.data)
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
        visibleAnnouncementList.clear()
        visibleAnnouncementList.addAll(commonAnnouncementList.filter {
            it.time in timeFromMilli until timeToMilli
        }.toList())
        _state.value = Announcements(visibleAnnouncementList.toList())
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
        return buildString {
            append(convertTimeDateFromMillisToString(timeFrom, "dd MMM yyyy"), "\n")
            append(convertTimeDateFromMillisToString(timeFrom, "HH:mm"), " - ")
            append(convertTimeDateFromMillisToString(timeTo, "HH:mm"))
        }
    }
}