package nik.borisov.vmannouncement.utils

import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.domain.entities.MessageItem
import nik.borisov.vmannouncement.domain.entities.TelegramBot

interface TelegramBotHelper : TimeConverter {

    suspend fun sendMessages(
        bot: TelegramBot,
        announcementsList: List<AnnouncementItem>,
        useCase: suspend (MessageItem) -> Unit
    ) {
        if (announcementsList.size > 30) {
            val subLists = announcementsList.chunked(30)
            for (list in subLists) {
                sendMessage(bot, parseMessage(list), useCase)
            }
        } else {
            sendMessage(bot, parseMessage(announcementsList), useCase)
        }
        sendMessage(bot, parseShortAnnouncementsMessage(announcementsList), useCase)
    }

    private suspend fun sendMessage(
        bot: TelegramBot,
        message: String,
        useCase: suspend (MessageItem) -> Unit
    ) {
        useCase(MessageItem(bot = bot, messageText = message))
    }

    private fun parseMessage(announcements: List<AnnouncementItem>): String {
        return buildString {
            for (announcement in announcements) {
                append(announcement.announcementText, "\n\n")
            }
        }
    }

    private fun parseShortAnnouncementsMessage(announcements: List<AnnouncementItem>): String {
        val counterMap = mutableMapOf<String, MutableMap<String, Int>>()
        for (announcement in announcements) {
            val time = convertTimeDateFromMillisToString(announcement.time, "dd MMM HH:mm")
            var leagueList = announcement.league.split(". ").take(2)
            if (!leagueList.contains("Women")) leagueList = leagueList.take(1)
            val league = leagueList.joinToString(". ")
            if (counterMap.containsKey(time)) {
                val map = counterMap[time]!!
                if (map.containsKey(league)) {
                    var count = map[league]!!
                    map[league] = ++count
                } else {
                    map[league] = 1
                }
            } else {
                counterMap[time] = mutableMapOf(league to 1)
            }
        }
        return buildString {
            for (entry in counterMap) {
                append(entry.key, ":\n")
                for (secondEntry in entry.value) {
                    append(
                        "      ",
                        secondEntry.key,
                        " - ",
                        secondEntry.value,
                        "\n"
                    )

                }
                append("\n")
            }
        }
    }
}