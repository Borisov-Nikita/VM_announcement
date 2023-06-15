package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.repositories.Repository
import javax.inject.Inject

class DeleteAnnouncementsReportUseCase @Inject constructor(
    private val repository: Repository
) {

    suspend fun deleteAnnouncementsReport(reportId: Long) {
        return repository.deleteAnnouncementsReport(reportId)
    }
}