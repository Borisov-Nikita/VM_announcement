package nik.borisov.vmannouncement.domain.entities

data class MessageItem(

    val bot: TelegramBot,
    val messageText: String
)
