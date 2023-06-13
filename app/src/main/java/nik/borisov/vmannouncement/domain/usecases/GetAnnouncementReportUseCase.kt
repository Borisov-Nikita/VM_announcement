package nik.borisov.vmannouncement.domain.usecases

import androidx.lifecycle.LiveData
import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem
import nik.borisov.vmannouncement.domain.repositories.Repository

class GetAnnouncementReportUseCase(
    private val repository: Repository
) {

    fun getAnnouncementsReport(): LiveData<List<AnnouncementsReportItem>> {
        return repository.getAnnouncementsReport()
    }
}