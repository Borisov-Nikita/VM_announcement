package nik.borisov.vmannouncement.data.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey


@Entity(
    tableName = "announcements",
    foreignKeys = [
        ForeignKey(
            entity = AnnouncementsReportDbModel::class,
            parentColumns = ["id"],
            childColumns = ["id_announcements_report"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class AnnouncementItemDbModel(

    @PrimaryKey(autoGenerate = true)
    val id: Long,
    @ColumnInfo(name = "id_announcements_report")
    val announcementsReportId: Long,
    @ColumnInfo(name = "sport_name")
    val sport: String,
    @ColumnInfo(name = "league_name")
    val league: String,
    @ColumnInfo(name = "announcement_time")
    val time: Long,
    @ColumnInfo(name = "first_team_name")
    val firstTeam: String,
    @ColumnInfo(name = "second_team_name")
    val secondTeam: String,
    @ColumnInfo(name = "announcement_text")
    val announcementText: String
)
