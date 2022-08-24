package com.example.alarmapplication.ui

import android.content.Context
import android.util.Log
import com.example.alarmapplication.data.AlarmRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AlarmModule {

    val applicationContext: Context

    constructor(applicationContext: Context) {
        this.applicationContext = applicationContext
    }

    @Provides
    @Singleton
    fun provideApplicationContext(): Context {
        return applicationContext
    }
}