package com.example.alarmapplication.ui

import com.example.alarmapplication.data.AlarmRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
class AlarmModule {
    @Provides
    fun provideAlarmRepository(): AlarmRepository {
        return AlarmRepository()
    }
}