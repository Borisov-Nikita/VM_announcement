package nik.borisov.vmannouncement.presentation.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nik.borisov.vmannouncement.data.RepositoryImpl
import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem
import nik.borisov.vmannouncement.domain.usecases.DeleteAnnouncementsReportUseCase
import nik.borisov.vmannouncement.domain.usecases.GetAnnouncementReportUseCase

class SavedReportsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = RepositoryImpl(application)
    private val getAnnouncementReportUseCase = GetAnnouncementReportUseCase(repository)
    private val deleteAnnouncementsReportUseCase = DeleteAnnouncementsReportUseCase(repository)

    fun getAnnouncementReports(): LiveData<List<AnnouncementsReportItem>> {
        return getAnnouncementReportUseCase.getAnnouncementsReport()
    }

    fun deleteAnnouncementReport(reportId: Long) {
        viewModelScope.launch {
            deleteAnnouncementsReportUseCase.deleteAnnouncementsReport(reportId)
        }
    }
}