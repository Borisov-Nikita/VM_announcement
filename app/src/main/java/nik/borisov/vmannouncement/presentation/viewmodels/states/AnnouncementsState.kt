package nik.borisov.vmannouncement.presentation.viewmodels.states

import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.utils.DataResult

sealed class AnnouncementsState

class Announcements(val announcements: List<AnnouncementItem>) : AnnouncementsState()
class Line(val line: DataResult<String>) : AnnouncementsState()
object BotError : AnnouncementsState()
