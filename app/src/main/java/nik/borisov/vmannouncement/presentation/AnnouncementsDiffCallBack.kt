package nik.borisov.vmannouncement.presentation

import androidx.recyclerview.widget.DiffUtil
import nik.borisov.vmannouncement.domain.entities.AnnouncementItem

class AnnouncementsDiffCallBack : DiffUtil.ItemCallback<AnnouncementItem>() {

    override fun areItemsTheSame(oldItem: AnnouncementItem, newItem: AnnouncementItem): Boolean {
        return oldItem.firstTeam == newItem.firstTeam
    }

    override fun areContentsTheSame(oldItem: AnnouncementItem, newItem: AnnouncementItem): Boolean {
        return oldItem == newItem
    }
}