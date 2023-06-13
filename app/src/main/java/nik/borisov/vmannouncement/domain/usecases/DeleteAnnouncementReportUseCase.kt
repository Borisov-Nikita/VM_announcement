package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.repositories.Repository

class DeleteAnnouncementReportUseCase(
    private val repository: Repository
) {

    suspend fun deleteAnnouncementsReport(reportId: Long) {
        return repository.deleteAnnouncementReport(reportId)
    }
}