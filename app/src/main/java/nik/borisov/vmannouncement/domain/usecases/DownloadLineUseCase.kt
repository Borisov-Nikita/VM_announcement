package nik.borisov.vmannouncement.domain.usecases

import nik.borisov.vmannouncement.domain.repositories.Repository
import nik.borisov.vmannouncement.utils.DataResult
import javax.inject.Inject

class DownloadLineUseCase @Inject constructor(
    private val repository: Repository
) {

    suspend fun downloadLine(
        firstTeam: String,
        secondTeam: String,
        time: Long
    ): DataResult<String> {
        return repository.downloadLine(firstTeam, secondTeam, time)
    }
}