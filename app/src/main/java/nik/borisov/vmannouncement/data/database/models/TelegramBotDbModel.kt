package nik.borisov.vmannouncement.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "telegram_bot")
data class TelegramBotDbModel(

    @PrimaryKey
    val id: Int = 1,
    @ColumnInfo(name = "token")
    val token: String,
    @ColumnInfo(name = "chat_id")
    val chatId: String
)
