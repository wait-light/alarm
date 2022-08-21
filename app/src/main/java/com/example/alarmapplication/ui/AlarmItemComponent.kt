package com.example.alarmapplication.ui

import com.example.alarmapplication.AlarmComponent
import dagger.Module
import dagger.Subcomponent

@Subcomponent
interface AlarmItemComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AlarmItemComponent
    }

    fun inject(alarmAddFragment: AlarmAddFragment)
}

@Module(subcomponents = [AlarmItemComponent::class])
interface AlarmItemModule{

}