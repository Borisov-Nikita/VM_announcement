package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.repositories.Repository

class DeleteAnnouncementsReportUseCase(
    private val repository: Repository
) {

    suspend fun deleteAnnouncementsReport(reportId: Long) {
        return repository.deleteAnnouncementsReport(reportId)
    }
}