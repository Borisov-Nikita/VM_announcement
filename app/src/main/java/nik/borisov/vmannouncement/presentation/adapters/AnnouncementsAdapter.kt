package nik.borisov.vmannouncement.presentation.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import nik.borisov.vmannouncement.databinding.AnnouncementItemBinding
import nik.borisov.vmannouncement.domain.entities.AnnouncementItem

class AnnouncementsAdapter :
    ListAdapter<AnnouncementItem, AnnouncementViewHolder>(AnnouncementsDiffCallBack()) {

    var onAnnouncementClickListener: ((AnnouncementItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnnouncementViewHolder {
        val binding = AnnouncementItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AnnouncementViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AnnouncementViewHolder, position: Int) {
        val announcement = currentList[position]
        holder.binding.announcementTextView.text = announcement.announcementText
        holder.binding.announcementCardView.setOnClickListener {
            onAnnouncementClickListener?.invoke(announcement)
        }
    }
}