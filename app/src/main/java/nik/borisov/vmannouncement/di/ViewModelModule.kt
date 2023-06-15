package nik.borisov.vmannouncement.di

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import nik.borisov.vmannouncement.presentation.viewmodels.SavedAnnouncementsViewModel
import nik.borisov.vmannouncement.presentation.viewmodels.SavedReportsViewModel
import nik.borisov.vmannouncement.presentation.viewmodels.SearchAnnouncementsViewModel
import nik.borisov.vmannouncement.presentation.viewmodels.TelegramBotSettingsViewModel

@Module
interface ViewModelModule {

    @IntoMap
    @ViewModelKey(SavedAnnouncementsViewModel::class)
    @Binds
    fun bindSavedAnnouncementsViewModel(impl: SavedAnnouncementsViewModel): ViewModel

    @IntoMap
    @ViewModelKey(SavedReportsViewModel::class)
    @Binds
    fun bindSavedReportsViewModel(impl: SavedReportsViewModel): ViewModel

    @IntoMap
    @ViewModelKey(SearchAnnouncementsViewModel::class)
    @Binds
    fun bindSearchAnnouncementsViewModel(impl: SearchAnnouncementsViewModel): ViewModel

    @IntoMap
    @ViewModelKey(TelegramBotSettingsViewModel::class)
    @Binds
    fun bindTelegramBotSettingsViewModel(impl: TelegramBotSettingsViewModel): ViewModel
}