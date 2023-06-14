package nik.borisov.vmannouncement.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import nik.borisov.vmannouncement.databinding.ReportItemBinding
import nik.borisov.vmannouncement.domain.entities.AnnouncementsReportItem

class ReportsAdapter : ListAdapter<AnnouncementsReportItem, ReportViewHolder>(ReportsDiffCallBack()) {

    var onReportClickListener: ((Long) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ReportItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = currentList[position]
        holder.binding.announcementTextView.text = report.info
        holder.binding.announcementCardView.setOnClickListener {
            onReportClickListener?.invoke(report.id)
        }
    }
}