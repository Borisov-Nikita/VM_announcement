package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.repositories.Repository
import javax.inject.Inject

class DeleteAnnouncementUseCase @Inject constructor(
    private val repository: Repository
) {

    suspend fun deleteAnnouncement(announcementId: Long) {
        repository.deleteAnnouncement(announcementId)
    }
}