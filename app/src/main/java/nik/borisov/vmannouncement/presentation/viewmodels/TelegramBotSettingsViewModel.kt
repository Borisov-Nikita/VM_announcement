package nik.borisov.vmannouncement.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nik.borisov.vmannouncement.domain.entities.MessageItem
import nik.borisov.vmannouncement.domain.entities.TelegramBot
import nik.borisov.vmannouncement.domain.usecases.AddTelegramBotUseCase
import nik.borisov.vmannouncement.domain.usecases.GetTelegramBotUseCase
import nik.borisov.vmannouncement.domain.usecases.SendTelegramMessageUseCase
import nik.borisov.vmannouncement.presentation.viewmodels.states.Bot
import nik.borisov.vmannouncement.presentation.viewmodels.states.Error
import nik.borisov.vmannouncement.presentation.viewmodels.states.Finish
import nik.borisov.vmannouncement.presentation.viewmodels.states.TelegramBotSettingsState
import nik.borisov.vmannouncement.utils.DataResult
import javax.inject.Inject


class TelegramBotSettingsViewModel @Inject constructor(
    private val getTelegramBotUseCase: GetTelegramBotUseCase,
    private val addTelegramBotUseCase: AddTelegramBotUseCase,
    private val sendTelegramMessageUseCase: SendTelegramMessageUseCase
) : ViewModel() {

    private val _state = MutableLiveData<TelegramBotSettingsState>()
    val state: LiveData<TelegramBotSettingsState>
        get() = _state

    init {
        getTelegramBot()
    }

    fun addTelegramBot(tokenInput: String, chatIdInput: String) {
        val bot = TelegramBot(tokenInput, chatIdInput)
        viewModelScope.launch {
            val isTelegramBotValid = isTelegramBotValid(bot)
            if (isTelegramBotValid) {
                addTelegramBotUseCase.addTelegramBot(bot)
                _state.value = Finish
            }
        }
    }

    private suspend fun isTelegramBotValid(bot: TelegramBot): Boolean {
        var isValid = false
        val result = sendTelegramMessageUseCase.sendTelegramMessage(
            MessageItem(bot, "It is test message")
        )
        if (result is DataResult.Success) {
            isValid = true
        } else {
            _state.value = Error("${result.message}")
        }
        return isValid
    }

    private fun getTelegramBot() {
        viewModelScope.launch {
            val bot = getTelegramBotUseCase.getTelegramBot()
            if (bot != null) _state.value = Bot(bot)
        }
    }
}