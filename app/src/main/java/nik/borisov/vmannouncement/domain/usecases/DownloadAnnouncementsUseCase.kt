package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.entities.AnnouncementItem
import nik.borisov.vmannouncement.domain.repositories.Repository
import nik.borisov.vmannouncement.utils.DataResult
import javax.inject.Inject

class DownloadAnnouncementsUseCase @Inject constructor(
    private val repository: Repository
) {

    suspend fun downloadAnnouncements(date: Long): DataResult<List<AnnouncementItem>> {
        return repository.downloadAnnouncements(date)
    }
}