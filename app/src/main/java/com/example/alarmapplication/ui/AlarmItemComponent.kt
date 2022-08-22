package com.example.alarmapplication.ui

import com.example.alarmapplication.AlarmComponent
import dagger.Module
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
}

@Module(subcomponents = [AlarmItemComponent::class])
interface AlarmItemModule {

}