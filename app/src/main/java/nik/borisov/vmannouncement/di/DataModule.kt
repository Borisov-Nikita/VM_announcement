package nik.borisov.vmannouncement.di

import android.app.Application
import dagger.Binds
import dagger.Module
import dagger.Provides
import nik.borisov.vmannouncement.data.RepositoryImpl
import nik.borisov.vmannouncement.data.database.AppDatabase
import nik.borisov.vmannouncement.data.database.dao.AnnouncementsDao
import nik.borisov.vmannouncement.data.network.ApiFactory
import nik.borisov.vmannouncement.data.network.services.MarathonBetApiService
import nik.borisov.vmannouncement.data.network.services.OneXStavkaApiService
import nik.borisov.vmannouncement.data.network.services.TelegramBotApiService
import nik.borisov.vmannouncement.domain.repositories.Repository

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindRepository(impl: RepositoryImpl): Repository

    companion object {

        @ApplicationScope
        @Provides
        fun provideAnnouncementsDao(application: Application): AnnouncementsDao {
            return AppDatabase.getInstance(application).getAnnouncementDao()
        }

        @ApplicationScope
        @Provides
        fun provideMarathonBetApiService(): MarathonBetApiService {
            return ApiFactory.marathonBetApiService
        }

        @ApplicationScope
        @Provides
        fun provideOneXStavkaApiService(): OneXStavkaApiService {
            return ApiFactory.oneXStavkaApiService
        }

        @ApplicationScope
        @Provides
        fun provideTelegramBotApiService(): TelegramBotApiService {
            return ApiFactory.telegramBotApiService
        }
    }
}