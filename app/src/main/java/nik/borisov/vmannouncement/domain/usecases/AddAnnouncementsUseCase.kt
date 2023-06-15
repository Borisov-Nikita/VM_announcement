package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.domain.repositories.Repository
import javax.inject.Inject

class AddAnnouncementsUseCase @Inject constructor(
    private val repository: Repository
) {

    suspend fun addAnnouncement(announcementList: List<AnnouncementItem>) {
        return repository.addAnnouncements(announcementList)
    }
}