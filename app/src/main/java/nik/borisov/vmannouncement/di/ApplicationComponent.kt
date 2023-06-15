package nik.borisov.vmannouncement.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import nik.borisov.vmannouncement.presentation.MainActivity

@ApplicationScope
@Component(modules = [ViewModelModule::class, DataModule::class])
interface ApplicationComponent {

    fun inject(activity: MainActivity)

    @Component.Factory
    interface ApplicationComponentFactory {

        fun create(
            @BindsInstance application: Application
        ): ApplicationComponent
    }
}