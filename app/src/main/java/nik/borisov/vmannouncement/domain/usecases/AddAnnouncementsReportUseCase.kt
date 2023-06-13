package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem
import nik.borisov.vmannouncement.domain.repositories.Repository

class AddAnnouncementsReportUseCase(
    private val repository: Repository
) {

    suspend fun addAnnouncementsReport(report: AnnouncementsReportItem): Long {
        return repository.addAnnouncementsReport(report)
    }
}