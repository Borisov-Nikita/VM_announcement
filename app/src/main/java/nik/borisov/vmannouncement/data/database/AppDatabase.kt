package nik.borisov.vmannouncement.data.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import nik.borisov.vmannouncement.data.database.dao.AnnouncementsDao
import nik.borisov.vmannouncement.data.database.models.AnnouncementItemDbModel
import nik.borisov.vmannouncement.data.database.models.AnnouncementsReportDbModel
import nik.borisov.vmannouncement.data.database.models.TelegramBotDbModel

@Database(
    entities = [
        AnnouncementItemDbModel::class,
        AnnouncementsReportDbModel::class,
        TelegramBotDbModel::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getAnnouncementDao(): AnnouncementsDao

    companion object {

        private const val DB_NAME = "application.db"

        private var INSTANCE: AppDatabase? = null
        private val LOCK = Any()

        fun getInstance(application: Application): AppDatabase {
            INSTANCE?.let {
                return it
            }
            synchronized(LOCK) {
                INSTANCE?.let {
                    return it
                }
                val database = Room.databaseBuilder(
                    application,
                    AppDatabase::class.java,
                    DB_NAME
                ).build()
                INSTANCE = database
                return database
            }
        }
    }
}