package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.entities.TelegramBot
import nik.borisov.vmannouncement.domain.repositories.Repository

class AddTelegramBotUseCase(
    private val repository: Repository
) {

    suspend fun addTelegramBot(bot: TelegramBot) {
        return repository.addTelegramBot(bot)
    }
}