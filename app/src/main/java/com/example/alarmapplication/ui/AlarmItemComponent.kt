package com.example.alarmapplication.ui

import com.example.alarmapplication.AlarmComponent
import com.example.alarmapplication.data.AlarmService
import com.example.alarmapplication.domain.AlarmDomain
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import javax.inject.Singleton

@Subcomponent
interface AlarmItemComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AlarmItemComponent
    }

    fun inject(alarmAddFragment: AlarmAddFragment)
    fun inject(alarmListFragment: AlarmListFragment)
    fun inject(alarmService: AlarmService)
}

@Module(subcomponents = [AlarmItemComponent::class])
class AlarmItemModule {

}