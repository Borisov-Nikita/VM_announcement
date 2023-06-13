package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.repositories.Repository

class DeleteAnnouncementUseCase(
    private val repository: Repository
) {

    suspend fun deleteAnnouncement(announcementId: Long) {
        repository.deleteAnnouncement(announcementId)
    }
}