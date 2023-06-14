package nik.borisov.vmannouncement.presentation.adapters

import androidx.recyclerview.widget.DiffUtil
import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem

class ReportsDiffCallBack : DiffUtil.ItemCallback<AnnouncementsReportItem>() {
    override fun areItemsTheSame(
        oldItem: AnnouncementsReportItem,
        newItem: AnnouncementsReportItem
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: AnnouncementsReportItem,
        newItem: AnnouncementsReportItem
    ): Boolean {
        return oldItem == newItem
    }
}