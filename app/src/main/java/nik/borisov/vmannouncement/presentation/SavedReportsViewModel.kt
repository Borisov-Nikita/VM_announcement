package nik.borisov.vmannouncement.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nik.borisov.vmannouncement.data.RepositoryImpl
import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem
import nik.borisov.vmannouncement.domain.usecases.DeleteAnnouncementReportUseCase
import nik.borisov.vmannouncement.domain.usecases.GetAnnouncementReportUseCase

class SavedReportsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RepositoryImpl(application)
    private val getAnnouncementReportUseCase = GetAnnouncementReportUseCase(repository)
    private val deleteAnnouncementReportUseCase = DeleteAnnouncementReportUseCase(repository)

    fun getAnnouncementReports(): LiveData<List<AnnouncementsReportItem>> {
        return getAnnouncementReportUseCase.getAnnouncementsReport()
    }

    fun deleteAnnouncementReport(reportId: Long) {
        viewModelScope.launch {
            deleteAnnouncementReportUseCase.deleteAnnouncementsReport(reportId)
        }
    }
}