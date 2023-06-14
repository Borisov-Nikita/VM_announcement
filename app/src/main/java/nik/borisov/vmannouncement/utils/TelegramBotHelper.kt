package nik.borisov.vmannouncement.utils

import nik.borisov.vmannouncement.domain.entities.AnnouncementItem

interface TelegramBotHelper : TimeConverter {

    fun parseMessage(announcements: List<AnnouncementItem>): String {
        return buildString {
            for (announcement in announcements) {
                append(announcement.announcementText, "\n\n")
            }
        }
    }

    fun parseShortAnnouncementsMessage(announcements: List<AnnouncementItem>): String {
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
                    append("${secondEntry.key} - ${secondEntry.value}", "\n")
                }
                append("\n")
            }
        }
    }
}