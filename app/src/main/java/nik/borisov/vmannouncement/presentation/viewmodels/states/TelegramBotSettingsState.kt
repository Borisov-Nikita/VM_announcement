package nik.borisov.vmannouncement.presentation.viewmodels.states

import nik.borisov.vmannouncement.domain.entities.TelegramBot

sealed class TelegramBotSettingsState

class Bot(val bot: TelegramBot) : TelegramBotSettingsState()
class Error(val error: String) : TelegramBotSettingsState()
object Finish : TelegramBotSettingsState()

