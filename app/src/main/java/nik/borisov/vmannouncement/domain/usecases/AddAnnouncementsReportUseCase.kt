package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem
import nik.borisov.vmannouncement.domain.repositories.Repository
import javax.inject.Inject

class AddAnnouncementsReportUseCase @Inject constructor(
    private val repository: Repository
) {

    suspend fun addAnnouncementsReport(report: AnnouncementsReportItem): Long {
        return repository.addAnnouncementsReport(report)
    }
}