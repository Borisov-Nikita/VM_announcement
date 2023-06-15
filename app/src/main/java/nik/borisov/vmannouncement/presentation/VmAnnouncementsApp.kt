package nik.borisov.vmannouncement.presentation

import android.app.Application
import nik.borisov.vmannouncement.di.DaggerApplicationComponent

class VmAnnouncementsApp : Application() {

    val component by lazy {
        DaggerApplicationComponent.factory()
            .create(this)
    }
}