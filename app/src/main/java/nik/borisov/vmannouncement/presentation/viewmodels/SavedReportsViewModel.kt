package nik.borisov.vmannouncement.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem
import nik.borisov.vmannouncement.domain.usecases.DeleteAnnouncementsReportUseCase
import nik.borisov.vmannouncement.domain.usecases.GetAnnouncementReportUseCase
import javax.inject.Inject

class SavedReportsViewModel @Inject constructor(
    private val getAnnouncementReportUseCase: GetAnnouncementReportUseCase,
    private val deleteAnnouncementsReportUseCase: DeleteAnnouncementsReportUseCase
) : ViewModel() {

    fun getAnnouncementReports(): LiveData<List<AnnouncementsReportItem>> {
        return getAnnouncementReportUseCase.getAnnouncementsReport()
    }

    fun deleteAnnouncementReport(reportId: Long) {
        viewModelScope.launch {
            deleteAnnouncementsReportUseCase.deleteAnnouncementsReport(reportId)
        }
    }
}