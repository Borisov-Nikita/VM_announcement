package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.entities.TelegramBot
import nik.borisov.vmannouncement.domain.repositories.Repository

class GetTelegramBotUseCase(
    private val repository: Repository
) {

    suspend fun getTelegramBot(): TelegramBot? {
        return repository.getTelegramBot()
    }
}