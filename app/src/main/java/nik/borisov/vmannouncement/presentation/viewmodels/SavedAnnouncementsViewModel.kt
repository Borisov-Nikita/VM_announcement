package nik.borisov.vmannouncement.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nik.borisov.vmannouncement.data.RepositoryImpl
import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.domain.entities.MessageItem
import nik.borisov.vmannouncement.domain.entities.TelegramBot
import nik.borisov.vmannouncement.domain.usecases.*
import nik.borisov.vmannouncement.presentation.viewmodels.states.AnnouncementsState
import nik.borisov.vmannouncement.presentation.viewmodels.states.BotError
import nik.borisov.vmannouncement.presentation.viewmodels.states.Line
import nik.borisov.vmannouncement.utils.TelegramBotHelper

class SavedAnnouncementsViewModel(application: Application) : AndroidViewModel(application),
    TelegramBotHelper {

    private val repository = RepositoryImpl(application)
    private val getAnnouncementsUseCase = GetAnnouncementsUseCase(repository)
    private val downloadLineUseCase = DownloadLineUseCase(repository)
    private val deleteAnnouncementUseCase = DeleteAnnouncementUseCase(repository)
    private val sendTelegramMessageUseCase =
        SendTelegramMessageUseCase(repository)
    private val getTelegramBotUseCase = GetTelegramBotUseCase(repository)

    private val _state = MutableLiveData<AnnouncementsState>()
    val state: LiveData<AnnouncementsState>
        get() = _state

    private var telegramBot: TelegramBot? = null

    init {
        getTelegramBot()
    }

    fun getAnnouncements(announcementsReportId: Long): LiveData<List<AnnouncementItem>> {
        return getAnnouncementsUseCase.getAnnouncements(announcementsReportId)
    }

    fun getLine(firstTeam: String, secondTeam: String, time: Long) {
        viewModelScope.launch {
            _state.value = Line(downloadLineUseCase.downloadLine(firstTeam, secondTeam, time))
        }
    }

    fun deleteAnnouncement(announcementId: Long) {
        viewModelScope.launch {
            deleteAnnouncementUseCase.deleteAnnouncement(announcementId)
        }
    }

    fun sendAnnouncementsReport(announcements: List<AnnouncementItem>) {
        if (telegramBot != null) {
            viewModelScope.launch {
                sendTelegramMessageUseCase.sendTelegramMessage(
                    MessageItem(
                        bot = telegramBot ?: return@launch,
                        messageText = parseMessage(announcements)
                    )
                )
                sendTelegramMessageUseCase.sendTelegramMessage(
                    MessageItem(
                        bot = telegramBot ?: return@launch,
                        messageText = parseShortAnnouncementsMessage(announcements)
                    )
                )
            }
        } else {
            _state.value = BotError
        }
    }

    private fun getTelegramBot() {
        viewModelScope.launch {
            telegramBot = getTelegramBotUseCase.getTelegramBot()
        }
    }
}