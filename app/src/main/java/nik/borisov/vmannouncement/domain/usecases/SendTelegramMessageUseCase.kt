package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.entities.MessageItem
import nik.borisov.vmannouncement.domain.repositories.Repository
import nik.borisov.vmannouncement.utils.DataResult

class SendTelegramMessageUseCase(
    private val repository: Repository
) {

    suspend fun sendTelegramMessage(message: MessageItem): DataResult<Unit> {
        return repository.sendTelegramMessage(message)
    }
}