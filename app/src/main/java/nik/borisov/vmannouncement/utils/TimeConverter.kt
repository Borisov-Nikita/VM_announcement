package nik.borisov.vmannouncement.utils

import java.text.SimpleDateFormat
import java.util.*

interface TimeConverter {

    fun convertTimeDateFromMillisToString(time: Long, pattern: String): String {
        val formatter = SimpleDateFormat(pattern, Locale.ENGLISH)
        formatter.timeZone = TimeZone.getTimeZone("Europe/Moscow")
        return formatter.format(Date(time))
    }
}