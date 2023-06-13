package nik.borisov.vmannouncement.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "announcements_report")
data class AnnouncementsReportDbModel(

    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "info")
    val info: String
)
