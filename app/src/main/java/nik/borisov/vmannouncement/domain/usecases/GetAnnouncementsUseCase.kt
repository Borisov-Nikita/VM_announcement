package nik.borisov.vmannouncement.domain.usecases

import androidx.lifecycle.LiveData
import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.domain.repositories.Repository
import javax.inject.Inject

class GetAnnouncementsUseCase @Inject constructor(
    private val repository: Repository
) {

    fun getAnnouncements(reportId: Long): LiveData<List<AnnouncementItem>> {
        return repository.getAnnouncements(reportId)
    }
}