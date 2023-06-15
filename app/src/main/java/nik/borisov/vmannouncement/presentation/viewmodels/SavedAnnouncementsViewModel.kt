package nik.borisov.vmannouncement.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.domain.entities.MessageItem
import nik.borisov.vmannouncement.domain.entities.TelegramBot
import nik.borisov.vmannouncement.domain.usecases.*
import nik.borisov.vmannouncement.presentation.viewmodels.states.AnnouncementsState
import nik.borisov.vmannouncement.presentation.viewmodels.states.BotError
import nik.borisov.vmannouncement.presentation.viewmodels.states.Line
import nik.borisov.vmannouncement.utils.TelegramBotHelper
import javax.inject.Inject

class SavedAnnouncementsViewModel @Inject constructor(
    private val getAnnouncementsUseCase: GetAnnouncementsUseCase,
    private val downloadLineUseCase: DownloadLineUseCase,
    private val deleteAnnouncementUseCase: DeleteAnnouncementUseCase,
    private val sendTelegramMessageUseCase: SendTelegramMessageUseCase,
    private val getTelegramBotUseCase: GetTelegramBotUseCase,
) : ViewModel(), TelegramBotHelper {


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
                sendMessages(
                    telegramBot ?: return@launch,
                    announcements,
                    ::sendMessageWithUseCase
                )
            }
        } else {
            _state.value = BotError
        }
    }

    private suspend fun sendMessageWithUseCase(messageItem: MessageItem) {
        sendTelegramMessageUseCase.sendTelegramMessage(messageItem)
    }

    private fun getTelegramBot() {
        viewModelScope.launch {
            telegramBot = getTelegramBotUseCase.getTelegramBot()
        }
    }
}