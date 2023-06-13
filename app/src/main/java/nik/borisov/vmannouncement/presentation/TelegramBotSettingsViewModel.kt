package nik.borisov.vmannouncement.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nik.borisov.vmannouncement.data.RepositoryImpl
import nik.borisov.vmannouncement.domain.entities.MessageItem
import nik.borisov.vmannouncement.domain.entities.TelegramBot
import nik.borisov.vmannouncement.domain.usecases.AddTelegramBotUseCase
import nik.borisov.vmannouncement.domain.usecases.GetTelegramBotUseCase
import nik.borisov.vmannouncement.domain.usecases.SendTelegramMessageUseCase
import nik.borisov.vmannouncement.utils.DataResult

class TelegramBotSettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RepositoryImpl(application)
    private val getTelegramBotUseCase = GetTelegramBotUseCase(repository)
    private val addTelegramBotUseCase = AddTelegramBotUseCase(repository)
    private val sendTelegramMessageUseCase = SendTelegramMessageUseCase(repository)

    private val _bot = MutableLiveData<TelegramBot>()
    val bot: LiveData<TelegramBot>
        get() = _bot

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    private val _shouldCloseScreen = MutableLiveData<Unit>()
    val shouldCloseScreen: LiveData<Unit>
        get() = _shouldCloseScreen

    init {
        getTelegramBot()
    }

    fun addTelegramBot(tokenInput: String, chatIdInput: String) {
        val bot = parseInput(tokenInput, chatIdInput)
        if (bot != null) {
            viewModelScope.launch {
                val isTelegramBotValid = isTelegramBotValid(bot)
                if (isTelegramBotValid) {
                    addTelegramBotUseCase.addTelegramBot(bot)
                    _shouldCloseScreen.value = Unit
                }
            }
        } else {
            _error.value = "Invalid chat id input"
        }
    }

    private fun parseInput(tokenInput: String, chatIdInput: String): TelegramBot? {
        return try {
            TelegramBot(
                token = tokenInput,
                chatId = chatIdInput
            )
        } catch (e: Exception) {
            null
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
            _error.value = "Something went wrong:\n${result.message}"
        }
        return isValid
    }

    private fun getTelegramBot() {
        viewModelScope.launch {
             _bot.value = getTelegramBotUseCase.getTelegramBot()
        }
    }
}